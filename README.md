# Detector de Mutantes - Mercadolibre Challenge

**Autor:** Cecilia Calvo - Legajo 46332  
**Proyecto:** Global Mercadolibre - Detector de ADN Mutante

## 📋 Descripción

Aplicación que detecta si un humano es mutante basándose en su secuencia de ADN. Un humano es mutante si se encuentran **más de una secuencia** de cuatro letras iguales (A, T, C, G) de forma horizontal, vertical u oblicua.

## 🚀 Tecnologías Utilizadas

- **Java 17**
- **Maven** - Gestión de dependencias
- **H2 Database** - Base de datos en memoria
- **Spark Framework** - API REST
- **Lombok** - Reducción de código boilerplate
- **Jackson** - Manejo de JSON
- **JUnit 5** - Testing

## 📁 Arquitectura del Proyecto

```
src/main/java/ar/edu/unlam/parcial/
├── app/           - Clase principal y configuración de endpoints
├── dto/           - Data Transfer Objects
├── exception/     - Excepciones personalizadas
├── model/         - Entidades del dominio
├── repository/    - Capa de persistencia
├── service/       - Lógica de negocio
└── utils/         - Utilidades y validadores
```

## 🔧 Instalación y Ejecución

### Prerequisitos
- Java 17 o superior
- Maven 3.6+

### Pasos para ejecutar

1. **Clonar el repositorio**
```bash
git clone <tu-repositorio>
cd parcial-mercadolibre-mutantes-calvo
```

2. **Compilar el proyecto**
```bash
mvn clean install
```

3. **Ejecutar la aplicación**
```bash
mvn exec:java -Dexec.mainClass="ar.edu.unlam.parcial.app.App"
```

La aplicación se ejecutará en `http://localhost:4567`

## 📡 Endpoints API

### POST /mutant
Detecta si una secuencia de ADN corresponde a un mutante.

**Request:**
```json
{
  "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]
}
```

**Response:**
- **200 OK** - Si es mutante
```json
{
  "isMutant": true,
  "message": "Mutante detectado"
}
```

- **403 Forbidden** - Si es humano
```json
{
  "isMutant": false,
  "message": "Humano"
}
```

- **400 Bad Request** - Si el ADN es inválido
```json
{
  "timestamp": "2024-11-29T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Secuencia de ADN inválida...",
  "path": "/mutant"
}
```

### GET /stats
Obtiene estadísticas de verificaciones de ADN.

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

## 🧪 Ejecutar Tests

```bash
mvn test
```

**Cobertura de tests:**
- 16 tests unitarios
- Casos de mutantes (horizontal, vertical, diagonal)
- Casos de humanos
- Validaciones de entrada
- Casos borde

## 🎯 Características Implementadas

### Nivel 1 ✅
- [x] Función `isMutant(String[] dna)`
- [x] Detección de secuencias horizontales
- [x] Detección de secuencias verticales
- [x] Detección de secuencias diagonales
- [x] Detección de 2+ secuencias (mutante)

### Nivel 2 ✅
- [x] API REST con Spark Framework
- [x] Endpoint POST /mutant
- [x] Respuestas HTTP 200 (mutante) y 403 (humano)
- [x] Manejo de errores con status 400

### Nivel 3 ✅
- [x] Base de datos H2
- [x] Persistencia de ADN verificados
- [x] Deduplicación por hash (1 registro por ADN)
- [x] Endpoint GET /stats
- [x] Cálculo de ratio mutantes/humanos

### Adicionales ✅
- [x] DTOs para requests/responses
- [x] GlobalExceptionHandler
- [x] Excepciones personalizadas
- [x] Validación de secuencias
- [x] Tests unitarios completos
- [x] Arquitectura en capas

## 📊 Algoritmo de Detección

El algoritmo verifica la matriz de ADN en 4 direcciones:
1. **Horizontal** - Busca 4 letras iguales consecutivas en filas
2. **Vertical** - Busca 4 letras iguales consecutivas en columnas
3. **Diagonal \** - Busca en diagonales de izquierda a derecha
4. **Diagonal /** - Busca en diagonales de derecha a izquierda

**Optimizaciones:**
- Terminación temprana al encontrar 2+ secuencias
- No usa estructuras auxiliares innecesarias
- Acceso directo a caracteres de la matriz

## 🔐 Validaciones

- Matriz debe ser NxN (cuadrada)
- Solo acepta caracteres: A, T, C, G
- No acepta secuencias nulas o vacías
- No acepta matrices menores a 4x4

## 💾 Base de Datos

**Tabla: genetic_sequences**
- `id` - IDENTITY PRIMARY KEY
- `sequence` - VARCHAR(1000) - Secuencia de ADN
- `dna_hash` - VARCHAR(64) UNIQUE - Hash para deduplicación
- `is_mutant` - BOOLEAN - Indica si es mutante

## 👤 Autor

**Cecilia Calvo**  
Legajo: 46332  
Universidad Tecnologica Nacional FRM

## 📝 Notas

- La base de datos H2 está configurada en modo memoria (`jdbc:h2:mem:parcialdb`)
- Los datos se pierden al reiniciar la aplicación
- Para producción, configurar H2 en modo archivo o usar PostgreSQL

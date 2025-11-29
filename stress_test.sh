#!/bin/bash

echo "╔════════════════════════════════════════════════╗"
echo "║   🔥 PRUEBAS DE STRESS - API MUTANTES         ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Contadores
MUTANTS_SUCCESS=0
MUTANTS_FAIL=0
HUMANS_SUCCESS=0
HUMANS_FAIL=0
START_TIME=$(date +%s)

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}[TEST 1/4]${NC} Enviando 50 peticiones POST /mutant (Mutantes)..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

for i in {1..50}
do
  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:4567/mutant \
    -H "Content-Type: application/json" \
    -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}')
  
  if [ "$RESPONSE" == "200" ]; then
    ((MUTANTS_SUCCESS++))
    echo -ne "${GREEN}✓${NC}"
  else
    ((MUTANTS_FAIL++))
    echo -ne "${RED}✗${NC}"
  fi
  
  # Nueva línea cada 10 requests
  if [ $((i % 10)) -eq 0 ]; then
    echo -ne " [$i/50]\n"
  fi
done

echo ""
echo -e "${GREEN}✅ Test 1 completado:${NC} $MUTANTS_SUCCESS exitosos, $MUTANTS_FAIL fallidos"
echo ""

# Pequeña pausa
sleep 1

echo -e "${BLUE}[TEST 2/4]${NC} Enviando 50 peticiones POST /mutant (Humanos)..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

for i in {1..50}
do
  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost:4567/mutant \
    -H "Content-Type: application/json" \
    -d '{"dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"]}')
  
  if [ "$RESPONSE" == "403" ]; then
    ((HUMANS_SUCCESS++))
    echo -ne "${GREEN}✓${NC}"
  else
    ((HUMANS_FAIL++))
    echo -ne "${RED}✗${NC}"
  fi
  
  if [ $((i % 10)) -eq 0 ]; then
    echo -ne " [$i/50]\n"
  fi
done

echo ""
echo -e "${GREEN}✅ Test 2 completado:${NC} $HUMANS_SUCCESS exitosos, $HUMANS_FAIL fallidos"
echo ""

sleep 1

echo -e "${BLUE}[TEST 3/4]${NC} Enviando 100 peticiones mixtas concurrentes..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

for i in {1..100}
do
  # Alternar entre mutantes y humanos
  if [ $((i % 2)) -eq 0 ]; then
    curl -s -X POST http://localhost:4567/mutant \
      -H "Content-Type: application/json" \
      -d '{"dna":["AAAAGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}' > /dev/null &
  else
    curl -s -X POST http://localhost:4567/mutant \
      -H "Content-Type: application/json" \
      -d '{"dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TTTTTT"]}' > /dev/null &
  fi
  
  echo -ne "${YELLOW}⚡${NC}"
  
  if [ $((i % 20)) -eq 0 ]; then
    echo -ne " [$i/100]\n"
  fi
done

# Esperar a que terminen todas las peticiones concurrentes
wait

echo ""
echo -e "${GREEN}✅ Test 3 completado:${NC} 100 peticiones concurrentes enviadas"
echo ""

sleep 1

echo -e "${BLUE}[TEST 4/4]${NC} Consultando estadísticas finales (GET /stats)..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

STATS=$(curl -s http://localhost:4567/stats)

echo -e "${YELLOW}Respuesta:${NC}"
echo "$STATS" | python3 -m json.tool 2>/dev/null || echo "$STATS"

echo ""

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║          🎉 PRUEBAS COMPLETADAS               ║"
echo "╠════════════════════════════════════════════════╣"
echo "║  Total de peticiones:      200                ║"
echo "║  Mutantes exitosos:        $MUTANTS_SUCCESS                   ║"
echo "║  Humanos exitosos:         $HUMANS_SUCCESS                   ║"
echo "║  Fallos totales:           $((MUTANTS_FAIL + HUMANS_FAIL))                    ║"
echo "║  Tiempo total:             ${DURATION}s                   ║"
echo "╚════════════════════════════════════════════════╝"
echo ""
echo -e "${GREEN}✅ Listo para tomar screenshots${NC}"

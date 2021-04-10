#!/bin/bash

teamsCount=$(ls "Teams" | wc -l)
for ((i = 1; i <= $teamsCount; i++)); do
  chmod 555 "Teams/Team$i/​​​​FinalResult​​​​​​​​​​​​"

done

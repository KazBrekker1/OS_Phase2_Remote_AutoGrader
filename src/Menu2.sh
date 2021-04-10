#!/bin/bash

# Color Codes
red='\033[0;31m'
RED='\033[1;31m'
GREEN='\033[1;32m'
green='\033[0;32m'
blue='\033[0;34m'
BLUE='\033[1;34m'
cyan='\033[0;36m'
CYAN='\033[1;36m'
NC='\033[0m' # No Color

input1=$1

function initFileStructures() {
  # Read a value with [Default]
  sbj=$1
  prjctNumber=$2
  teamNumber=$3
  dueDate=$4

  sbj=${sbj:-OS}
  prjctNumber=${prjctNumber:=1}
  teamNumber=${teamNumber:=1}

  prefix="./Courses/$sbj/Projects/Project$prjctNumber/Teams/Team$teamNumber/Trials"
  examplePrefix="./Courses/$sbj/Projects/Project$prjctNumber/ExampleResults"

  if [ -d "./Courses/$sbj/Projects/Project$prjctNumber/Teams/Team$teamNumber" ]; then
    echo -e "File Structure Exists"

  else
    echo -e "Initializing File Structure"
    mkdir -p Courses/$sbj/Projects/Project$prjctNumber/{ExampleResults,Teams/Team$teamNumber/Trials}
    chmod 777 "./Courses"
    touch Courses/$sbj/{instructors,Projects/Project$prjctNumber/{ExampleResults/{answer1,grades},Teams/Team$teamNumber/{FinalResult,TeamInfo}}}
    chmod 774 ./Courses/$sbj/Projects/Project$prjctNumber/Teams/Team$teamNumber/FinalResult
    touch Courses/$sbj/Projects/Project$prjctNumber/dueDate.sh

    cp ./terminate.sh ./Courses/$sbj/Projects/Project$prjctNumber/dueDate.sh
    chmod u+x "./Courses/$sbj/Projects/Project$prjctNumber/dueDate.sh"
    dueDatePath="./Courses/$sbj/Projects/Project$prjctNumber/dueDate.sh"
    # Here it should set a sched on dueDate.sh
    if [ ! -s ./Courses/$sbj/Projects/Project$prjctNumber/ProjectInfo ]; then
      echo $dueDate >./Courses/$sbj/Projects/Project$prjctNumber/ProjectInfo
      echo $dueDatePath | at 23:59 $dueDate
    fi
    echo 100 >>"$examplePrefix/grades"
    echo 10 >>"$examplePrefix/answer1"
  fi
}

function test() {
  # Loop Through ExpectedOutput
  prefix=$1
  examplePrefix=$2
  expectedResultsCount=$(ls "$examplePrefix" | wc -l)
  expectedResultsCount="$(($expectedResultsCount - 1))"
  readarray -t grades <"$examplePrefix/grades"
  # Loop Through Team's Answer
  teamResultsFile="$prefix/Outputs"
  teamResultsCount=$(ls "$teamResultsFile" | wc -l)
  totalGrade=0

  # Compare Both Counts
  if [ $expectedResultsCount -eq $teamResultsCount ]; then
    for ((i = 1, x = 0; i <= $expectedResultsCount; i++, x++)); do
      if [ "$(<$teamResultsFile/answer$i)" = "$(<$examplePrefix/answer$i)" ]; then
        totalGrade=$(expr $totalGrade + ${grades[$x]})
      else
        echo "Line Is Different"
      fi
    done
    json_grade $totalGrade
  else
    json_grade 0
  fi
}

function chooseFile() {
#  echo "=============Submit=============="
  filePath=$1
  sbj=$2
  prjctNumber=$3
  teamNumber=$4

  sbj=${sbj:-OS}
  prjctNumber=${prjctNumber:=1}
  teamNumber=${teamNumber:=1}

  trialNumber=$(ls "./Courses/$sbj/Projects/Project$prjctNumber/Teams/Team$teamNumber/Trials" 2>/dev/null | wc -l)
  trialNumber=$(expr $trialNumber + 1)

  prefix="./Courses/$sbj/Projects/Project$prjctNumber/Teams/Team$teamNumber/Trials/Trial$trialNumber"
  infoPath="./Courses/$sbj/Projects/Project$prjctNumber/ProjectInfo"
  examplePrefix="./Courses/$sbj/Projects/Project$prjctNumber/ExampleResults"
  mkdir -p $prefix/Outputs
  touch $prefix/Outputs/answer1
  echo 10 >>$prefix/Outputs/answer1

  # Direct Errors into ErrorLog
  gcc "$filePath" -o "$prefix/success.out" 2>"$prefix/ErrorLog"
  sed -i '1i ======\nError Log File\n======' "$prefix/ErrorLog"
  echo "Null" >>"$prefix/RunTimeOutput"
  # Check If File Exists
  if [ -f "$prefix/success.out" ]; then
    chmod u+x "$prefix/success.out"
    "./$prefix/success.out" >"$prefix/RunTimeOutput"
    sed -i '1i ======\nRun Time Ouput\n======' "$prefix/RunTimeOutput"
    test $prefix $examplePrefix
  else
    echo -e "Your File Is Corrupted"
    echo
    json_grade 0
  fi
}

function json_grade() {
  submissionDate=$(date '+%d-%m-%Y %H:%M:%S')
  JSON_STRING=$(jq -n \
    --arg sn "$sbj" \
    --arg pn "$prjctNumber" \
    --arg tn "$teamNumber" \
    --arg trn "$trialNumber" \
    --arg subDate "$submissionDate" \
    --arg grd "$1" \
    '{Subject: $sn, ProjectNumber: $pn,TeamNumber: $tn, TrialNumber: $trn, DateSubmitted: $subDate ,Grade: $grd}')
  >"$prefix/Log"
  echo "$JSON_STRING" >>"$prefix/Log"
  cp "$prefix/Log" "$prefix/../../FinalResult"
  sed -i '1i ======\nLog File\n======' "$prefix/Log"
}

#echo "============== AutoGrader v1 ================="
case $1 in
"1")
  echo
  chooseFile $2 $3 $4 $5
  ;;
"2")
  initFileStructures $2 $3 $4 $5
  ;;
"3")
  break
  ;;
*) echo -e "invalid option $REPLY " ;;
esac

#echo "============= Have A Good Day ================"

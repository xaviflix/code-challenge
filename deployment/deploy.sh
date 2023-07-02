#!/usr/bin/env bash

Help()
{
   echo
   echo "======================================================================================================"
   echo "Call this script with the following options:"
   echo
   echo "help       Print this help menu"
   echo "app        Deploys only the application container"
   echo "infra      Deploys only the infrastructure containers"
   echo "all        Deploys the application and infrastructure containers"
   echo
   echo "Examples:"
   echo
   echo "./deploy.sh help"
   echo "./deploy.sh app"
   echo "======================================================================================================"
   echo
}

RunWithOptions()
{
  if [ "$1" == "app" ]; then
   DeployApp
   exit
  elif [ "$1" == "infra" ]; then
   DeployInfra
   exit
  elif [ "$1" == "all" ]; then
   DeployAll
   exit
  else
    Help
    exit
  fi
}

DeployApp()
{
  cd .. || exit
  ./mvnw clean package -Dmaven.test.skip
  mv ./target/CodeChallenge-0.0.1.jar ./deployment/CodeChallenge-0.0.1.jar
  cd deployment || exit
  docker stop application
  docker rm application
  docker rmi code-challenge-app
  docker compose --profile app-only up -d
  rm ./CodeChallenge-0.0.1.jar
}

DeployInfra()
{
  docker stop database
  docker stop sqs-queues
  docker rm database
  docker rm sqs-queues
  docker compose --profile infra-only up -d
}

DeployAll()
{
  cd .. || exit
  ./mvnw clean package -Dmaven.test.skip
  mv ./target/CodeChallenge-0.0.1.jar ./deployment/CodeChallenge-0.0.1.jar
  cd deployment || exit
  docker stop database
  docker stop sqs-queues
  docker stop application
  docker rm database
  docker rm sqs-queues
  docker rm application
  docker rmi code-challenge-app
  docker compose --profile app-with-infra up -d
  rm ./CodeChallenge-0.0.1.jar
}

RunWithOptions $1

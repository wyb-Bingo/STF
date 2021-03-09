pipeline {
  agent any
  stages {
    stage('build') {
      agent {
        docker {
          image 'maven:3-alpine'
          args '-v /root/.m2:/root/.m2'
        }

      }
      steps {
        sh 'mvn -B -DskipTests clean install'
      }
    }

    stage('deploy') {
      steps {
        sh 'docker images | grep stf'
        sh 'docker ps -a | grep stf| awk \'{print $1}\'| xargs docker stop'
        sh 'docker run -p 8081:8081 -d stf:0.0.1-SNAPSHOT --name stf'
        sh 'docker ps -a | grep stf'
      }
    }

  }
}
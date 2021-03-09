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
        sh 'docker stop stf:0.0.1-SNAPSHOT'
        sh 'docker ps -a | grep stf| awk \'{print $1}\'| xargs docker rm -f'
        sh 'docker rmi stf:0.0.1-SNAPSHOT'
        sh 'mvn docker:build'
      }
    }

    stage('deploy') {
      steps {
        sh 'docker images | grep stf'
        sh 'docker run -p 8081:8081-d stf:0.0.1-SNAPSHOT --name stf'
        sh 'docker ps -a | grep stf'
      }
    }

  }
}
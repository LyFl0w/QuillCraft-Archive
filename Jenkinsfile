node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'Default Maven';
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=LyFl0w_SQLRequest_AYxV6ddIzyg8ZLlGuzXP"
    }
  }
}

pipeline {
  agent none
  
  stage('SCM') {
    agent any
    checkout scm
  }
  
  stages {
    stage("Build") {
      agent any
      steps {
        withMaven
        sh 'mvn clean package'
      }
    }
    
    stage("SonarQube analysis") {
      agent any
      steps {
        withSonarQubeEnv() {
          sh 'mvn sonar:sonar'
        }
      }
    }
    
    stage("Quality Gate") {
      agent any
      steps {
        timeout(time: 1, unit: 'HOURS') {
          def qg = waitForQualityGate()
          if (qg.status != 'OK') {
            error "Pipeline aborted due to quality gate failure: ${qg.status}"
          }
        }
      }
    }

  }
}

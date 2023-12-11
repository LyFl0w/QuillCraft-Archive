pipeline {
  agent none
  
  stages {
    stage('SCM') {
      agent any
      checkout scm
    }
    
    stage("Build") {
      agent any
      steps {
        withMaven {
          sh 'mvn clean package'
        }
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

pipeline {
  agent none

  tools {
    tool name: 'Default Maven', type: 'maven'
  }
  
  stages {
    stage('SCM') {
      agent any
      steps {
        checkout scm
      }
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
        withSonarQubeEnv('SonarQube') {
          sh 'mvn sonar:sonar'
        }
      }
    }
    
    stage("Quality Gate") {
      agent any
      steps {
        script {
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
}

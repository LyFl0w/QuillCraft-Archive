pipeline {
  agent any

  tools {
    maven 'Default Maven'
  }
  
  stages {
    stage('SCM Checkout') {
      steps {
        checkout scm
      }
    }
    
    stage('Build') {
      steps {
        script {
          sh 'mvn clean install'
        }
      }
    }

    stage('Test') {
      steps {
        script {
          sh 'mvn test'
        }
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('SonarQube') {
          sh 'mvn sonar:sonar'
        }
      }
    }

    stage('Quality Gate') {
      steps {
        timeout(time: 2, unit: 'MINUTES') {
          script {
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

pipeline {
    agent any

    tools {
        maven 'Default Maven'
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mvn install'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}

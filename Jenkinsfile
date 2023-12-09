pipeline {
    agent any
    
    tool name: 'Default Maven', type: 'maven'

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

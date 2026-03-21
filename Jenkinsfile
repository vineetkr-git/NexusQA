pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out NexusQA from GitHub...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building NexusQA...'
                sh '/usr/share/maven/bin/mvn clean compile -q'
                echo 'Build successful!'
            }
        }

        stage('API Tests') {
            steps {
                echo 'Running API Tests - No browser needed...'
                sh '/usr/share/maven/bin/mvn test -Dtest=AuthApiTest -DfailIfNoTests=false'
            }
        }

        stage('DB Tests') {
            steps {
                echo 'Running DB Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=DBTest -DfailIfNoTests=false'
            }
        }

        stage('Security Tests') {
            steps {
                echo 'Running Security Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=SecurityTest -DfailIfNoTests=false'
            }
        }

        stage('Generate Report') {
            steps {
                echo 'Generating Allure Report...'
                sh '/usr/share/maven/bin/mvn allure:report || true'
                echo 'Report generated!'
            }
        }
    }

    post {
        success {
            echo 'NexusQA Pipeline PASSED - API + DB + Security Tests!'
        }
        failure {
            echo 'NexusQA Pipeline FAILED - Check console output!'
        }
        always {
            echo 'NexusQA Pipeline completed!'
        }
    }
}
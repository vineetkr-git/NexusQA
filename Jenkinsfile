pipeline {
    agent any

    stages {
        stage('🔍 Checkout') {
            steps {
                echo '📥 Checking out NexusQA...'
                checkout scm
            }
        }

        stage('🏗️ Build') {
            steps {
                echo '🔨 Building...'
                sh 'mvn clean compile -q'
            }
        }

        stage('🧪 Run All Tests') {
            steps {
                echo '🚀 Running 31 tests...'
                sh 'mvn test'
            }
        }

        stage('📊 Allure Report') {
            steps {
                echo '📊 Generating report...'
                sh 'mvn allure:report'
            }
        }
    }

    post {
        success {
            echo '✅ NexusQA — ALL 31 TESTS PASSED!'
        }
        failure {
            echo '❌ Pipeline FAILED — check logs!'
        }
    }
}
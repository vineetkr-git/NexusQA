pipeline {
    agent any

    environment {
        MAVEN_HOME = '/usr/share/maven'
        PATH = "/usr/share/maven/bin:${env.PATH}"
    }

    stages {
        stage('🔍 Checkout') {
            steps {
                echo '📥 Checking out NexusQA from GitHub...'
                checkout scm
                echo '✅ Code checked out successfully!'
            }
        }

        stage('🏗️ Build') {
            steps {
                echo '🔨 Building NexusQA...'
                sh '/usr/share/maven/bin/mvn clean compile -q'
                echo '✅ Build successful!'
            }
        }

        stage('🧪 Run All 31 Tests') {
            steps {
                echo '🚀 Running full NexusQA test suite...'
                sh '/usr/share/maven/bin/mvn test'
            }
        }

        stage('📊 Generate Allure Report') {
            steps {
                echo '📊 Generating Allure Report...'
                sh '/usr/share/maven/bin/mvn allure:report'
                echo '✅ Report generated!'
            }
        }
    }

    post {
        success {
            echo '''
            ================================================
            ✅ NexusQA Pipeline PASSED!
            🎉 All 31 Tests Passing!
            ================================================
            '''
        }
        failure {
            echo '''
            ================================================
            ❌ NexusQA Pipeline FAILED!
            Check console output for details
            ================================================
            '''
        }
        always {
            echo '📊 NexusQA Pipeline completed!'
        }
    }
}
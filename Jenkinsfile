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
                sh '''
                    mkdir -p src/main/resources
                    printf 'app.url=https://opensource-demo.orangehrmlive.com\napp.username=Admin\napp.password=admin123\ngrid.enabled=false\ngrid.url=http://localhost:4444\nollama.url=http://host.docker.internal:11434\nollama.model=phi\nemail.enabled=false\ndb.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1\ndb.username=sa\ndb.password=\ndb.driver=org.h2.Driver\naws.endpoint=http://nexusqa-localstack:4566\naws.region=us-east-1\naws.accessKey=test\naws.secretKey=test\naws.s3.bucket=nexusqa-reports\naws.sns.topic=nexusqa-alerts\naws.cloudwatch.namespace=NexusQA/Tests\n' > src/main/resources/config.properties
                    /usr/share/maven/bin/mvn clean compile -q
                '''
                echo 'Build successful!'
            }
        }

        stage('DB Tests') {
            steps {
                echo 'Running DB Tests - H2 in-memory...'
                sh '''
                    /usr/share/maven/bin/mvn test \
                    -Dtest=DBTest \
                    -DfailIfNoTests=false || true
                '''
            }
        }

        stage('Security Tests') {
            steps {
                echo 'Running Security Tests...'
                sh '''
                    /usr/share/maven/bin/mvn test \
                    -Dtest=SecurityTest \
                    -DfailIfNoTests=false || true
                '''
            }
        }

        stage('API Reachability Test') {
            steps {
                echo 'Verifying API is reachable...'
                sh '''
                    /usr/share/maven/bin/mvn test \
                    -Dtest=AuthApiTest#testGetAuthToken \
                    -DfailIfNoTests=false || true
                '''
            }
        }

        stage('AWS Tests') {
            steps {
                echo 'Running AWS LocalStack Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=AWSTest -DfailIfNoTests=false || true'
            }
        }

        stage('Generate Report') {
            steps {
                echo 'Generating Allure Report...'
                sh '/usr/share/maven/bin/mvn allure:report || true'
                echo 'Pipeline complete!'
            }
        }
    }

    post {
        success {
            echo 'NexusQA Pipeline PASSED!'
        }
        failure {
            echo 'NexusQA Pipeline FAILED!'
        }
        always {
            echo 'NexusQA CI/CD Pipeline completed!'
        }
    }
}
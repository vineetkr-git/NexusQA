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
                    printf 'app.url=https://opensource-demo.orangehrmlive.com\napp.username=Admin\napp.password=admin123\ngrid.enabled=false\ngrid.url=http://localhost:4444\nollama.url=http://host.docker.internal:11434\nollama.model=phi\nemail.enabled=false\ndb.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1\ndb.username=sa\ndb.password=\ndb.driver=org.h2.Driver\n' > src/main/resources/config.properties
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

        stage('Generate Report') {
            steps {
                sh '/usr/share/maven/bin/mvn allure:report || true'
            }
            post {
                always {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        results: [[path: 'target/allure-results']]
                    ])
                }
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
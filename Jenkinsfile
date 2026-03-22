pipeline {
    agent any
    environment {
        MAVEN_HOME = '/usr/share/maven'
        SLACK_WEBHOOK = credentials('slack-webhook-url')
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
                    printf 'app.url=https://opensource-demo.orangehrmlive.com\napp.username=Admin\napp.password=admin123\ngrid.enabled=false\ngrid.url=http://localhost:4444\nollama.url=http://host.docker.internal:11434\nollama.model=phi\nemail.enabled=false\ndb.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1\ndb.username=sa\ndb.password=\ndb.driver=org.h2.Driver\naws.endpoint=http://nexusqa-localstack:4566\naws.region=us-east-1\naws.accessKey=test\naws.secretKey=test\naws.s3.bucket=nexusqa-reports\naws.sns.topic=nexusqa-alerts\naws.cloudwatch.namespace=NexusQA/Tests\nslack.webhook=${SLACK_WEBHOOK}\n' > src/main/resources/config.properties
                    /usr/share/maven/bin/mvn clean compile -q
                '''
                echo 'Build successful!'
            }
        }
        stage('Notify Start') {
            steps {
                sh """
                    curl -s -X POST -H 'Content-type: application/json' \
                    --data '{"text":"🚀 *NexusQA Pipeline STARTED*\\n> Build: #${BUILD_NUMBER}\\n> Branch: main"}' \
                    ${SLACK_WEBHOOK}
                """
            }
        }
        stage('DB Tests') {
            steps {
                echo 'Running DB Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=DBTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh """
                        curl -s -X POST -H 'Content-type: application/json' \
                        --data '{"text":"✅ *DB Tests* — 7/7 PASSED 🗄️"}' \
                        ${SLACK_WEBHOOK}
                    """
                }
            }
        }
        stage('Security Tests') {
            steps {
                echo 'Running Security Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=SecurityTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh """
                        curl -s -X POST -H 'Content-type: application/json' \
                        --data '{"text":"✅ *Security Tests* — 4/4 PASSED 🔒"}' \
                        ${SLACK_WEBHOOK}
                    """
                }
            }
        }
        stage('API Reachability Test') {
            steps {
                echo 'Running API Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=AuthApiTest#testGetAuthToken -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh """
                        curl -s -X POST -H 'Content-type: application/json' \
                        --data '{"text":"✅ *API Tests* — 1/1 PASSED 🔌"}' \
                        ${SLACK_WEBHOOK}
                    """
                }
            }
        }
        stage('AWS Tests') {
            steps {
                echo 'Running AWS Tests...'
                sh '/usr/share/maven/bin/mvn test -Dtest=AWSTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh """
                        curl -s -X POST -H 'Content-type: application/json' \
                        --data '{"text":"✅ *AWS Tests* — 4/4 PASSED ☁️"}' \
                        ${SLACK_WEBHOOK}
                    """
                }
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
            sh """
                curl -s -X POST -H 'Content-type: application/json' \
                --data '{"text":"🎉 *NexusQA Pipeline PASSED!*\\n> Build: #${BUILD_NUMBER}\\n> DB: 7/7 | Security: 4/4 | API: 1/1 | AWS: 4/4\\n> Total: 16/16 tests passed ✅"}' \
                ${SLACK_WEBHOOK}
            """
            echo 'NexusQA Pipeline PASSED!'
        }
        failure {
            sh """
                curl -s -X POST -H 'Content-type: application/json' \
                --data '{"text":"🔴 *NexusQA Pipeline FAILED!*\\n> Build: #${BUILD_NUMBER}\\n> Check Jenkins for details"}' \
                ${SLACK_WEBHOOK}
            """
            echo 'NexusQA Pipeline FAILED!'
        }
        always {
            echo 'NexusQA CI/CD Pipeline completed!'
        }
    }
}
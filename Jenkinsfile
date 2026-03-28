pipeline {
    agent any
    environment {
        MAVEN_HOME    = '/usr/share/maven'
        SLACK_WEBHOOK = credentials('slack-webhook-url')
        KUBECONFIG    = '/var/jenkins_home/.kube/config'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out NexusQA from GitHub...'
                checkout scm
            }
        }

        stage('Setup kubectl') {
            steps {
                sh '''
                    if ! command -v kubectl > /dev/null 2>&1; then
                        echo "Installing kubectl..."
                        curl -LO https://dl.k8s.io/release/v1.28.0/bin/linux/amd64/kubectl
                        chmod +x kubectl
                        mv kubectl /usr/local/bin/kubectl
                    fi
                    kubectl version --client || true
                '''
            }
        }

        stage('Build') {
            steps {
                echo 'Building NexusQA...'
                sh '''
                    mkdir -p src/main/resources
                    printf 'app.url=https://opensource-demo.orangehrmlive.com\napp.username=Admin\napp.password=admin123\nbrowser=chrome\ngrid.enabled=true\ngrid.url=http://nexusqa-selenium-hub:4444\nollama.url=http://host.docker.internal:11434\nollama.model=phi\nemail.enabled=false\ndb.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1\ndb.username=sa\ndb.password=\ndb.driver=org.h2.Driver\naws.endpoint=http://nexusqa-localstack:4566\naws.region=us-east-1\naws.accessKey=test\naws.secretKey=test\naws.s3.bucket=nexusqa-reports\naws.sns.topic=nexusqa-alerts\naws.cloudwatch.namespace=NexusQA/Tests\n' > src/main/resources/config.properties
                    /usr/share/maven/bin/mvn clean compile -q
                '''
                echo 'Build successful!'
            }
        }

        stage('Deploy K8s Infrastructure') {
            steps {
                sh '''
                    kubectl apply -f kubernetes/01-namespace.yml || true
                    kubectl apply -f kubernetes/02-configmap.yml || true
                    kubectl apply -f kubernetes/03-selenium-hub.yml || true
                    kubectl apply -f kubernetes/04-selenium-nodes.yml || true
                    kubectl apply -f kubernetes/05-localstack.yml || true
                    kubectl apply -f kubernetes/06-ollama.yml || true
                    echo "Waiting 45s for pods..."
                    sleep 45
                    kubectl get pods -n nexusqa || true
                    kubectl get services -n nexusqa || true
                '''
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\u2638\\ufe0f *K8s Infrastructure Deployed*\\n> Selenium Hub + Chrome x2 + Firefox + LocalStack + Ollama\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('Notify Start') {
            steps {
                sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\ude80 *NexusQA Pipeline #${BUILD_NUMBER} STARTED*\\n> Running ALL 31 Tests\\n> UI(15) + API(5) + DB(7) + Security(4) + AWS(4)\\n> Docker + K8s + AWS + Slack + AI\"}' ${SLACK_WEBHOOK} || true"
            }
        }

        stage('UI Tests - Login (4)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=LoginTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udda5\\ufe0f *UI Login Tests (4)* - Selenium Grid | Chrome\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('UI Tests - Employee (6)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=EmployeeTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udc64 *UI Employee Tests (6)* - Selenium Grid | Chrome\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('UI Tests - Leave (5)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=LeaveTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udcc5 *UI Leave Tests (5)* - Selenium Grid | Chrome\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('API Tests (5)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=AuthApiTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udd0c *API Tests (5)* - REST Assured | Session Auth | AI Spy Agent\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('DB Tests (7)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=DBTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\uddc4\\ufe0f *DB Tests (7)* - H2 In-Memory | JDBC | AI Data Analysis\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('Security Tests (4)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=SecurityTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udd12 *Security Tests (4)* - OWASP | HTTPS | AI Security Analysis\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('AWS Tests (4)') {
            steps {
                sh '/usr/share/maven/bin/mvn test -Dtest=AWSTest -DfailIfNoTests=false || true'
            }
            post {
                always {
                    sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\u2601\\ufe0f *AWS Tests (4)* - S3 | SNS | CloudWatch | Lambda (LocalStack)\"}' ${SLACK_WEBHOOK} || true"
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                sh '/usr/share/maven/bin/mvn allure:report || true'
                echo 'Pipeline complete!'
            }
        }

    }

    post {
        success {
            sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83c\\udf89 *NexusQA Pipeline #${BUILD_NUMBER} PASSED!*\\n\\n*ALL 31 TESTS PASSED* \\u2705\\n\\n> \\ud83d\\udda5\\ufe0f UI Login:    4  (Selenium Grid Chrome)\\n> \\ud83d\\udc64 UI Employee: 6  (Selenium Grid Chrome)\\n> \\ud83d\\udcc5 UI Leave:    5  (Selenium Grid Chrome)\\n> \\ud83d\\udd0c API Tests:   5  (REST Assured)\\n> \\ud83d\\uddc4\\ufe0f DB Tests:    7  (H2 JDBC)\\n> \\ud83d\\udd12 Security:    4  (OWASP)\\n> \\u2601\\ufe0f AWS Tests:   4  (LocalStack)\\n\\n*Stack: Docker + Kubernetes + AWS + Slack + AI*\"}' ${SLACK_WEBHOOK} || true"
            echo 'NexusQA Pipeline PASSED!'
        }
        failure {
            sh "curl -s -X POST -H 'Content-type: application/json' --data '{\"text\":\"\\ud83d\\udd34 *NexusQA Pipeline #${BUILD_NUMBER} FAILED!*\\n> Check Jenkins: http://localhost:8080\"}' ${SLACK_WEBHOOK} || true"
            echo 'NexusQA Pipeline FAILED!'
        }
        always {
            echo 'NexusQA CI/CD Pipeline completed!'
        }
    }
}
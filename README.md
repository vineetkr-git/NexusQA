# 🚀 NexusQA — AI-Powered Enterprise Test Automation Framework

> **Author:** Vineet Kumar | **Version:** 1.0.0 | **Java 17 + Maven 3.9**

NexusQA is a full-stack, enterprise-grade test automation framework that combines **Selenium UI testing**, **REST API testing**, **Database validation**, **Security scanning**, **AI-powered analysis**, **AWS cloud integration**, **Kubernetes orchestration**, **Docker containerization**, and **Slack notifications** — all in one unified CI/CD pipeline powered by Jenkins.

---

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [How Everything Works Together](#how-everything-works-together)
- [Docker — What & Why](#docker--what--why)
- [Selenium Grid — What & Why](#selenium-grid--what--why)
- [Jenkins CI/CD — What & Why](#jenkins-cicd--what--why)
- [AWS LocalStack — What & Why](#aws-localstack--what--why)
- [Kubernetes — What & Why](#kubernetes--what--why)
- [Slack Notifications — What & Why](#slack-notifications--what--why)
- [AI Agents (Ollama) — What & Why](#ai-agents-ollama--what--why)
- [Test Suites](#test-suites)
- [Debug Flow](#debug-flow)
- [Quick Start](#quick-start)
- [Jenkins Pipeline](#jenkins-pipeline)
- [Configuration](#configuration)

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                        NexusQA Architecture                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│   Developer Push → GitHub → Jenkins CI/CD Pipeline                   │
│                                    │                                  │
│            ┌───────────────────────┼───────────────────────┐         │
│            │                       │                       │         │
│         DB Tests              Security Tests           API Tests      │
│         (H2/JDBC)            (OWASP Headers)        (REST Assured)   │
│            │                       │                       │         │
│            └───────────────────────┼───────────────────────┘         │
│                                    │                                  │
│                              AWS Tests                                │
│                    (S3 + SNS + CloudWatch + Lambda)                   │
│                                    │                                  │
│                         Kubernetes Deploy                             │
│              (Hub + Chrome×2 + Firefox + LocalStack)                 │
│                                    │                                  │
│                         Allure Report Generated                       │
│                                    │                                  │
│                      Slack Notifications Sent                         │
│                    (#nexusqa-alerts channel)                          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Category | Technology | Version | Purpose |
|---|---|---|---|
| Language | Java | 17 | Core framework |
| Build | Maven | 3.9.14 | Dependency management |
| UI Testing | Selenium | 4.18.1 | Browser automation |
| Driver Mgmt | WebDriverManager | 5.7.0 | Auto driver setup |
| Test Runner | TestNG | 7.9.0 | Test orchestration |
| API Testing | REST Assured | 5.4.0 | HTTP API validation |
| DB Testing | H2 + JDBC | 2.2.224 | In-memory DB tests |
| Security | OWASP Headers | - | Security scanning |
| AI Engine | Ollama (phi) | latest | AI test analysis |
| Reporting | Allure | 2.25.0 | Test reports |
| Reporting | Extent Reports | 5.1.1 | HTML reports |
| CI/CD | Jenkins | latest | Pipeline automation |
| Container | Docker | latest | Containerization |
| Grid | Selenium Grid | 4.18.1 | Parallel browsers |
| Cloud | AWS LocalStack | 3.0 | Local AWS simulation |
| Orchestration | Kubernetes | 1.34 | Container orchestration |
| Notifications | Slack | - | Team alerts |
| IDE | IntelliJ IDEA | 2025.3.4 | Development |

---

## 📁 Project Structure

```
NexusQA/
├── src/
│   ├── main/java/com/nexusqa/
│   │   ├── config/
│   │   │   └── ConfigManager.java          ← Loads config.properties
│   │   ├── core/
│   │   │   └── BaseTest.java               ← ThreadLocal WebDriver setup
│   │   ├── ui/pages/
│   │   │   ├── BasePage.java               ← Common page actions
│   │   │   ├── LoginPage.java              ← OrangeHRM login
│   │   │   ├── DashboardPage.java          ← Dashboard interactions
│   │   │   ├── EmployeePage.java           ← Employee management
│   │   │   └── LeavePage.java              ← Leave management
│   │   ├── api/
│   │   │   ├── clients/ApiClient.java      ← Session-based auth
│   │   │   └── endpoints/OrangeHRMEndpoints.java
│   │   ├── db/
│   │   │   └── DBManager.java              ← JDBC connection pool
│   │   ├── agents/
│   │   │   ├── OllamaAgent.java            ← Ollama HTTP client
│   │   │   └── AgentFactory.java           ← 5 AI agents
│   │   ├── aws/
│   │   │   └── AWSManager.java             ← S3+SNS+CW+Lambda
│   │   ├── notifications/
│   │   │   └── SlackNotifier.java          ← Slack webhook
│   │   ├── security/
│   │   │   └── ZapManager.java             ← OWASP ZAP
│   │   └── reporting/
│   │       ├── ExtentReportManager.java    ← Dark theme reports
│   │       └── ReportListener.java         ← TestNG listener
│   │
│   ├── test/java/com/nexusqa/
│   │   ├── ui/
│   │   │   ├── LoginTest.java              ← 4 UI login tests
│   │   │   ├── EmployeeTest.java           ← 6 UI employee tests
│   │   │   └── LeaveTest.java              ← 5 UI leave tests
│   │   ├── api/
│   │   │   └── AuthApiTest.java            ← 5 API tests
│   │   ├── db/
│   │   │   └── DBTest.java                 ← 7 DB tests
│   │   ├── security/
│   │   │   └── SecurityTest.java           ← 4 security tests
│   │   └── aws/
│   │       └── AWSTest.java                ← 4 AWS tests
│   │
│   └── main/resources/
│       └── config.properties               ← (gitignored - sensitive)
│
├── docker/
│   └── selenium-grid/
│       └── docker-compose.yml              ← Grid + LocalStack
│
├── kubernetes/
│   ├── 01-namespace.yml                    ← nexusqa namespace
│   ├── 02-configmap.yml                    ← All config in K8s
│   ├── 03-selenium-hub.yml                 ← Hub deployment
│   ├── 04-selenium-nodes.yml               ← Chrome×2 + Firefox
│   ├── 05-localstack.yml                   ← AWS LocalStack
│   ├── 06-ollama.yml                       ← AI engine
│   └── 07-nexusqa-job.yml                  ← Test runner job
│
├── testng.xml                              ← Test suite config
├── pom.xml                                 ← Maven dependencies
├── Jenkinsfile                             ← CI/CD pipeline
└── README.md                               ← This file
```

---

## 🔄 How Everything Works Together

```
┌─────────────────────────────────────────────────────────────────┐
│                    FULL FLOW DIAGRAM                             │
│                                                                   │
│  1. Developer writes test → git push → GitHub                    │
│                                  │                               │
│  2. Jenkins detects push → pulls code                            │
│                                  │                               │
│  3. Jenkins creates config.properties (with all keys)            │
│                                  │                               │
│  4. Maven compiles 20 source + 7 test files                      │
│                                  │                               │
│  5. Slack → "🚀 Pipeline STARTED #BuildNo"                       │
│                                  │                               │
│  ┌───────────────────────────────┼──────────────────────────┐   │
│  │           TEST STAGES         │                           │   │
│  │                               ▼                           │   │
│  │  DB Tests ──────────────► H2 in-memory DB                │   │
│  │  (7 tests)                    │ JDBC queries              │   │
│  │                               │ Ollama AI analysis        │   │
│  │                               │ Slack → "✅ DB 7/7"       │   │
│  │                               │                           │   │
│  │  Security Tests ──────────► OWASP header scan            │   │
│  │  (4 tests)                    │ HTTPS enforcement         │   │
│  │                               │ Ollama AI analysis        │   │
│  │                               │ Slack → "✅ SEC 4/4"      │   │
│  │                               │                           │   │
│  │  API Tests ───────────────► REST Assured                  │   │
│  │  (5 tests)                    │ OrangeHRM endpoints       │   │
│  │                               │ Session auth              │   │
│  │                               │ Slack → "✅ API 5/5"      │   │
│  │                               │                           │   │
│  │  AWS Tests ───────────────► LocalStack                    │   │
│  │  (4 tests)                    │ S3 upload report          │   │
│  │                               │ SNS send alert            │   │
│  │                               │ CloudWatch metrics        │   │
│  │                               │ Lambda invoke             │   │
│  │                               │ Slack → "✅ AWS 4/4"      │   │
│  └───────────────────────────────┼──────────────────────────┘   │
│                                  │                               │
│  6. K8s Deploy → namespace nexusqa                               │
│     • selenium-hub (1 pod)                                       │
│     • selenium-chrome (2 pods)                                   │
│     • selenium-firefox (1 pod)                                   │
│     • localstack (1 pod)                                         │
│     • ollama (1 pod)                                             │
│     Slack → "☸️ K8s Deploy DONE"                                 │
│                                  │                               │
│  7. Allure report generated                                      │
│                                  │                               │
│  8. Slack → "🎉 Pipeline PASSED! 20/20 tests"                    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🐳 Docker — What & Why

### What is Docker?
Docker is a containerization platform that packages applications and their dependencies into isolated **containers**. Think of a container as a lightweight VM that runs the same way on any machine.

### Why Docker in NexusQA?
Without Docker, running Selenium tests requires manually installing browsers and drivers on every machine. With Docker, we spin up pre-configured browser containers instantly.

### How NexusQA uses Docker:

```
docker-compose.yml
│
├── nexusqa-selenium-hub      ← The traffic controller
│   └── Port 4444             ← Jenkins/tests connect here
│   └── Port 4442/4443        ← Node communication
│
├── nexusqa-chrome-1          ← Chrome browser #1
│   └── Port 7900             ← VNC viewer (watch tests live!)
│   └── Connects to hub       ← Registers automatically
│
├── nexusqa-chrome-2          ← Chrome browser #2
│   └── Port 7901             ← VNC viewer
│
├── nexusqa-firefox           ← Firefox browser
│   └── Connects to hub
│
└── nexusqa-localstack        ← AWS simulator
    └── Port 4566             ← All AWS API calls go here
```

### Start Docker infrastructure:
```bash
cd docker/selenium-grid
docker-compose up -d
```

### Watch tests running live:
- Chrome 1: http://localhost:7900 (password: `secret`)
- Chrome 2: http://localhost:7901 (password: `secret`)
- Grid UI: http://localhost:4444/ui

### Debug Docker issues:
```bash
# Check all containers
docker ps

# View logs
docker logs nexusqa-selenium-hub
docker logs nexusqa-localstack

# Restart a container
docker restart nexusqa-chrome-1

# Check network
docker network ls
docker network inspect selenium-grid_default
```

---

## 🌐 Selenium Grid — What & Why

### What is Selenium Grid?
Selenium Grid allows running tests on multiple browsers and machines **in parallel**. It has a **Hub** (coordinator) and **Nodes** (browsers).

### Why Selenium Grid in NexusQA?
- Run tests on Chrome AND Firefox simultaneously
- Parallel test execution = faster results
- Headless browser testing in CI/CD (no display needed)
- Scale from 1 to 100 browser instances

### How it works in NexusQA:

```
Test Code
    │
    ▼
RemoteWebDriver(hub:4444)   ← BaseTest.java creates this
    │
    ▼
Selenium Hub (4444)          ← Routes to available node
    │
    ├──► Chrome Node 1       ← Runs LoginTest, EmployeeTest
    ├──► Chrome Node 2       ← Runs LeaveTest, APITest
    └──► Firefox Node        ← Cross-browser validation
```

### BaseTest.java — How driver is created:
```java
// When grid.enabled=true in config:
driver = new RemoteWebDriver(
    new URL("http://selenium-hub:4444"),
    new ChromeOptions()
);

// When grid.enabled=false (local):
WebDriverManager.chromedriver().setup();
driver = new ChromeDriver();
```

### Debug Grid issues:
```bash
# Check grid status
curl http://localhost:4444/status

# Check registered nodes
curl http://localhost:4444/grid/api/nodes

# View grid UI
open http://localhost:4444/ui
```

---

## 🔧 Jenkins CI/CD — What & Why

### What is Jenkins?
Jenkins is an open-source automation server that runs your build, test, and deployment pipeline automatically every time code is pushed to GitHub.

### Why Jenkins in NexusQA?
- **Automated testing**: Every git push triggers the full test suite
- **No manual effort**: Tests run without anyone clicking anything
- **History**: See pass/fail trends across builds
- **Integration**: Connects Git + Maven + Docker + K8s + Slack + AWS

### Jenkins runs as a Docker container:
```
Container: nexusqa-jenkins
Port: 8080 (UI) + 50000 (agents)
Volume: jenkins_home (persists data)
Network: selenium-grid_default (talks to LocalStack)
```

### Pipeline stages flow:
```
Checkout SCM
    │
    ▼
Build (compile + create config.properties)
    │
    ▼
Notify Start → Slack
    │
    ▼
DB Tests (7 tests) → Slack ✅
    │
    ▼
Security Tests (4 tests) → Slack ✅
    │
    ▼
API Tests (5 tests) → Slack ✅
    │
    ▼
AWS Tests (4 tests) → Slack ✅
    │
    ▼
Deploy to Kubernetes → Slack ✅
    │
    ▼
Generate Allure Report
    │
    ▼
Slack Final Result 🎉
```

### Jenkins access:
- URL: http://localhost:8080
- User: admin / admin123
- Pipeline: NexusQA-Pipeline

### Debug Jenkins issues:
```bash
# View Jenkins logs
docker logs nexusqa-jenkins

# Restart Jenkins
docker restart nexusqa-jenkins

# Access Jenkins shell
docker exec -it nexusqa-jenkins bash

# Check workspace
docker exec nexusqa-jenkins ls /var/jenkins_home/workspace/NexusQA-Pipeline/
```

---

## ☁️ AWS LocalStack — What & Why

### What is LocalStack?
LocalStack is a fully functional local AWS cloud stack. It simulates AWS services (S3, SNS, CloudWatch, Lambda) on your local machine — **completely free**, no AWS account needed.

### Why LocalStack in NexusQA?
- Test AWS integrations without real AWS costs
- No internet needed for AWS feature testing
- Reset state between test runs
- Safe to use test credentials (`test`/`test`)

### AWS Services used in NexusQA:

```
LocalStack (port 4566)
│
├── S3 (Simple Storage Service)
│   └── Bucket: nexusqa-reports
│   └── Purpose: Store Allure reports & screenshots
│   └── Test: Upload HTML report → verify in S3
│
├── SNS (Simple Notification Service)
│   └── Topic: nexusqa-alerts
│   └── Purpose: Send alerts when tests fail
│   └── Test: Publish message → verify delivery
│
├── CloudWatch
│   └── Namespace: NexusQA/Tests
│   └── Purpose: Track test metrics & durations
│   └── Test: Push metrics → query dashboard
│
└── Lambda
    └── Function: nexusqa-trigger
    └── Purpose: Trigger test runs on demand
    └── Test: Invoke function → get response
```

### How AWSManager.java works:
```
AWSManager.getInstance()
    │
    ├── Reads config (aws.endpoint=http://localstack:4566)
    ├── Creates AWS SDK clients with LocalStack endpoint
    ├── Creates S3 bucket if not exists
    └── Creates SNS topic if not exists

uploadReport(path, key)  → PUT to S3
sendAlert(subject, msg)  → PUBLISH to SNS
publishMetric(name, val) → PUT to CloudWatch
invokeLambda(fn, payload)→ INVOKE Lambda
```

### Debug LocalStack:
```bash
# Check LocalStack health
curl http://localhost:4566/_localstack/health

# List S3 buckets
aws --endpoint-url=http://localhost:4566 s3 ls

# List SNS topics
aws --endpoint-url=http://localhost:4566 sns list-topics

# View CloudWatch metrics
aws --endpoint-url=http://localhost:4566 cloudwatch list-metrics
```

---

## ☸️ Kubernetes — What & Why

### What is Kubernetes?
Kubernetes (K8s) is a container orchestration platform that automatically deploys, scales, and manages containerized applications. It ensures your containers are always running, restarts failed pods, and balances load.

### Why Kubernetes in NexusQA?
- **Production-like environment**: Tests run in the same setup as production
- **Auto-healing**: If a Chrome node crashes, K8s restarts it automatically
- **Scaling**: Need more Chrome nodes? Change `replicas: 2` to `replicas: 10`
- **Resource management**: CPU/memory limits prevent one pod starving others
- **Industry standard**: K8s is used by every major tech company

### NexusQA K8s Architecture:

```
Kubernetes Cluster (docker-desktop)
│
└── Namespace: nexusqa
    │
    ├── selenium-hub (Deployment)
    │   └── 1 Pod: selenium/hub:4.18.1
    │   └── Service: ClusterIP:4444
    │   └── Purpose: Routes test requests to browser nodes
    │
    ├── selenium-chrome (Deployment)
    │   └── 2 Pods: selenium/node-chrome:4.18.1
    │   └── Resources: 2Gi RAM, 1 CPU each
    │   └── Purpose: Run Chrome browser tests in parallel
    │
    ├── selenium-firefox (Deployment)
    │   └── 1 Pod: selenium/node-firefox:4.18.1
    │   └── Purpose: Cross-browser Firefox testing
    │
    ├── localstack (Deployment)
    │   └── 1 Pod: localstack/localstack:3.0
    │   └── Service: ClusterIP:4566
    │   └── Purpose: AWS services simulation in cluster
    │
    └── ollama (Deployment)
        └── 1 Pod: ollama/ollama:latest
        └── Service: ClusterIP:11434
        └── Purpose: AI analysis inside cluster
```

### Key Kubernetes concepts used:

| Concept | File | Purpose |
|---|---|---|
| Namespace | 01-namespace.yml | Isolate NexusQA resources |
| ConfigMap | 02-configmap.yml | Store config.properties in K8s |
| Deployment | 03-06.yml | Manage pod lifecycle |
| Service | 03-05.yml | Internal DNS (selenium-hub:4444) |
| Job | 07-nexusqa-job.yml | Run tests once and complete |
| InitContainer | 07-nexusqa-job.yml | Wait for hub before starting |

### Deploy to K8s:
```bash
kubectl apply -f kubernetes/01-namespace.yml
kubectl apply -f kubernetes/02-configmap.yml
kubectl apply -f kubernetes/03-selenium-hub.yml
kubectl apply -f kubernetes/04-selenium-nodes.yml
kubectl apply -f kubernetes/05-localstack.yml
kubectl apply -f kubernetes/06-ollama.yml
```

### Debug K8s issues:
```bash
# Check all pods
kubectl get pods -n nexusqa

# Check pod details
kubectl describe pod <pod-name> -n nexusqa

# View pod logs
kubectl logs <pod-name> -n nexusqa

# View pod logs live
kubectl logs -f <pod-name> -n nexusqa

# Check services
kubectl get services -n nexusqa

# Execute shell in pod
kubectl exec -it <pod-name> -n nexusqa -- bash

# Check resource usage
kubectl top pods -n nexusqa

# Delete and recreate a deployment
kubectl rollout restart deployment/selenium-chrome -n nexusqa

# Delete everything and start fresh
kubectl delete namespace nexusqa
kubectl apply -f kubernetes/
```

---

## 💬 Slack Notifications — What & Why

### What is Slack Webhook?
A Slack Incoming Webhook is a URL that accepts JSON POST requests and posts messages to a Slack channel. No bot token or complex OAuth needed.

### Why Slack in NexusQA?
- **Instant visibility**: Team sees test results without checking Jenkins
- **Every stage**: Know exactly which stage passed or failed
- **Rich messages**: Formatted with emojis, test counts, build numbers
- **Mobile alerts**: Get notified on phone when pipeline fails at 3 AM

### Message flow:

```
Jenkins Pipeline
    │
    ▼
curl POST → https://hooks.slack.com/services/...
    │
    ▼
Slack API → #nexusqa-alerts channel
    │
    ▼
Team sees message instantly 📱
```

### Messages sent:

| Stage | Message |
|---|---|
| Pipeline Start | 🚀 NexusQA Pipeline STARTED #BuildNo |
| DB Tests | ✅ DB Tests PASSED — 7/7 🗄️ |
| Security Tests | ✅ Security Tests PASSED — 4/4 🔒 |
| API Tests | ✅ API Tests PASSED — 5/5 🔌 |
| AWS Tests | ✅ AWS Tests PASSED — 4/4 ☁️ |
| K8s Deploy | ☸️ Kubernetes Deployment DONE |
| Pipeline Pass | 🎉 Pipeline PASSED! 20/20 tests |
| Pipeline Fail | 🔴 Pipeline FAILED! Check Jenkins |

### SlackNotifier.java — How it works:
```java
SlackNotifier.getInstance(webhookUrl)
    │
    ├── send(message)
    │   └── Creates JSON: {"text": "message"}
    │   └── HTTP POST to webhook URL
    │   └── Returns 200 OK if successful
    │
    ├── sendPipelineStart()
    ├── sendStageResult(stage, passed, total, failures)
    └── sendPipelineResult(passed, total, failures)
```

### Webhook stored securely:
```
Jenkins → Manage Jenkins → Credentials
    └── ID: slack-webhook-url (Secret text)
    └── Referenced in Jenkinsfile as: ${SLACK_WEBHOOK}
    └── Never committed to GitHub ✅
```

### Debug Slack:
```bash
# Test webhook manually
curl -X POST -H 'Content-type: application/json' \
  --data '{"text":"Test from NexusQA!"}' \
  https://hooks.slack.com/services/YOUR/WEBHOOK/URL
```

---

## 🤖 AI Agents (Ollama) — What & Why

### What is Ollama?
Ollama is a local AI model runner. It runs large language models (like `phi`, `llama`, `mistral`) on your machine without sending data to external APIs.

### Why AI in NexusQA?
- **Smart bug analysis**: AI explains what test failures mean
- **DB anomaly detection**: AI spots unusual data patterns
- **Security insights**: AI provides recommendations beyond header checks
- **No API cost**: Runs 100% locally, no OpenAI/Claude API fees

### AI Agents in NexusQA:

```
AgentFactory
│
├── getBugAnalystAgent()      ← Analyzes test failures
├── getDataValidatorAgent()   ← Validates DB data quality
├── getSecurityAgent()        ← Reviews security findings
├── getApiReviewerAgent()     ← Reviews API responses
└── getPerformanceAgent()     ← Analyzes performance data
```

### How OllamaAgent works:
```
Test calls: agent.analyze(data)
    │
    ▼
HTTP POST → http://localhost:11434/api/generate
    Body: {model: "phi", prompt: "Analyze this: " + data}
    │
    ▼
phi model processes request (local GPU/CPU)
    │
    ▼
Response: AI analysis text
    │
    ▼
Test logs analysis + continues
```

---

## 🧪 Test Suites

### All 31 Tests:

```
UI Tests (15) — Selenium + Grid
├── LoginTest (4)
│   ├── TC_UI_001: Valid login
│   ├── TC_UI_002: Invalid login
│   ├── TC_UI_003: Empty credentials
│   └── TC_UI_004: Logout
├── EmployeeTest (6)
│   ├── TC_UI_005: Navigate to employees
│   ├── TC_UI_006: Search employee
│   ├── TC_UI_007: Add employee
│   ├── TC_UI_008: Edit employee
│   ├── TC_UI_009: View employee details
│   └── TC_UI_010: Employee count
└── LeaveTest (5)
    ├── TC_UI_011: Navigate to leave
    ├── TC_UI_012: View leave list
    ├── TC_UI_013: Apply leave
    ├── TC_UI_014: Leave balance
    └── TC_UI_015: Leave types

API Tests (5) — REST Assured
├── TC_API_001: Session established (reachability)
├── TC_API_002: GET employees list
├── TC_API_003: GET users
├── TC_API_004: GET leave types
└── TC_API_005: AI Spy Agent reviews response

DB Tests (7) — H2 + JDBC
├── TC_DB_001: DB connection
├── TC_DB_002: INSERT employees
├── TC_DB_003: SELECT all employees
├── TC_DB_004: SELECT by department
├── TC_DB_005: UPDATE salary
├── TC_DB_006: COUNT records
└── TC_DB_007: AI data quality analysis

Security Tests (4) — OWASP
├── TC_SEC_001: Security headers scan
├── TC_SEC_002: HTTPS enforcement
├── TC_SEC_003: No sensitive data in URL
└── TC_SEC_004: AI security analysis

AWS Tests (4) — LocalStack
├── TC_AWS_001: S3 report upload
├── TC_AWS_002: SNS alert notification
├── TC_AWS_003: CloudWatch metrics
└── TC_AWS_004: Lambda invocation
```

---

## 🔍 Debug Flow

### When a test fails, follow this flow:

```
Test FAILED
    │
    ├── 1. Check Jenkins console output
    │       http://localhost:8080
    │       → Click failed build → Console Output
    │
    ├── 2. Check Allure Report
    │       docker cp nexusqa-jenkins:/var/jenkins_home/workspace/
    │         NexusQA-Pipeline/target/site/allure-maven-plugin
    │         C:\Users\hp\NexusQA\allure-report
    │       → Open index.html
    │
    ├── 3. Check Slack #nexusqa-alerts
    │       → See which stage failed
    │
    ├── 4. For UI failures:
    │       → Check Grid UI: http://localhost:4444/ui
    │       → Watch VNC: http://localhost:7900
    │       → Check screenshots in Extent Report
    │
    ├── 5. For DB failures:
    │       → Check config.properties has db.url
    │       → Verify H2 connection string
    │
    ├── 6. For AWS failures:
    │       → Check LocalStack: curl localhost:4566/_localstack/health
    │       → Check Docker: docker ps | grep localstack
    │       → Check K8s: kubectl get pods -n nexusqa
    │
    ├── 7. For AI failures:
    │       → Check Ollama: curl localhost:11434/api/tags
    │       → Restart: docker restart ollama (if using Docker)
    │
    └── 8. For K8s failures:
            → kubectl describe pod <name> -n nexusqa
            → kubectl logs <name> -n nexusqa
```

### Common fixes:

| Problem | Fix |
|---|---|
| `config.properties not found` | Check Jenkins Build stage printf |
| `Selenium session not created` | Restart Chrome node container |
| `LocalStack connection refused` | `docker-compose up -d localstack` |
| `Ollama timeout` | Ollama AI tests take 60-160s, normal |
| `K8s pod Pending` | Not enough memory, reduce limits |
| `Slack notification failed` | Check webhook URL in Jenkins credentials |
| `Multiple HTTP implementations` | Remove url-connection-client from pom.xml |

---

## ⚡ Quick Start

### Prerequisites:
- Java 17
- Maven 3.9+
- Docker Desktop (with Kubernetes enabled)
- IntelliJ IDEA
- Ollama installed with `phi` model

### 1. Clone:
```bash
git clone https://github.com/vineetkr-git/NexusQA.git
cd NexusQA
```

### 2. Create config.properties:
```bash
# Copy template
cp src/main/resources/config.properties.template \
   src/main/resources/config.properties

# Edit with your values
```

### 3. Start Docker infrastructure:
```bash
cd docker/selenium-grid
docker-compose up -d
cd ../..
```

### 4. Start Ollama:
```bash
ollama serve
ollama pull phi
```

### 5. Run all tests locally:
```bash
mvn test -Dtest=DBTest,SecurityTest,AuthApiTest,AWSTest
```

### 6. Run in Jenkins:
```bash
# Start Jenkins
docker start nexusqa-jenkins

# Open browser
open http://localhost:8080
# Build Now → NexusQA-Pipeline
```

### 7. Deploy to Kubernetes:
```bash
kubectl apply -f kubernetes/
kubectl get pods -n nexusqa
```

---

## 🔄 Jenkins Pipeline

```groovy
// Jenkinsfile summary
pipeline {
    stages:
        Checkout        → Pull from GitHub
        Build           → mvn compile + create config
        Notify Start    → Slack message
        DB Tests        → 7 H2 database tests
        Security Tests  → 4 OWASP security tests
        API Tests       → 5 REST Assured tests
        AWS Tests       → 4 LocalStack AWS tests
        K8s Deploy      → Deploy infra to Kubernetes
        Generate Report → Allure HTML report
    post:
        success → Slack "🎉 PASSED"
        failure → Slack "🔴 FAILED"
}
```

---

## ⚙️ Configuration

### config.properties keys:

```properties
# Application
app.url=https://opensource-demo.orangehrmlive.com
app.username=Admin
app.password=admin123
browser=chrome

# Selenium Grid
grid.enabled=true/false
grid.url=http://selenium-hub:4444

# Ollama AI
ollama.url=http://localhost:11434
ollama.model=phi

# Database
db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
db.username=sa
db.password=
db.driver=org.h2.Driver

# AWS LocalStack
aws.endpoint=http://localhost:4566
aws.region=us-east-1
aws.accessKey=test
aws.secretKey=test
aws.s3.bucket=nexusqa-reports
aws.sns.topic=nexusqa-alerts
aws.cloudwatch.namespace=NexusQA/Tests

# Slack (stored in Jenkins credentials)
slack.webhook=https://hooks.slack.com/services/...
```

---

## 📊 Reports

### Allure Report:
```bash
# Generate
mvn allure:report

# Copy from Jenkins
docker cp nexusqa-jenkins:/var/jenkins_home/workspace/NexusQA-Pipeline/target/site/allure-maven-plugin C:\Users\hp\NexusQA\allure-report

# Open
start C:\Users\hp\NexusQA\allure-report\index.html
```

### Extent Report:
```bash
docker cp nexusqa-jenkins:/var/jenkins_home/workspace/NexusQA-Pipeline/reports/NexusQA_Latest_Report.html C:\Users\hp\NexusQA\
```

---

## 👤 Author

**Vineet Kumar**
- GitHub: [@vineetkr-git](https://github.com/vineetkr-git)
- Email: 1992vineetkumarsingh@gmail.com
- Framework: NexusQA v1.0.0

---

*Built with ❤️ using Java, Maven, Selenium, TestNG, REST Assured, H2, Allure, Extent Reports, Ollama, AWS SDK, LocalStack, Docker, Kubernetes, Jenkins, and Slack*

pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        // Helps Testcontainers work with Docker Engine 29+
        TESTCONTAINERS_RYUK_DISABLED = 'false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Backend Test') {
            steps {
                dir('backend') {
                    // Requires Docker available to the Jenkins agent (Docker-in-Docker or mounted socket)
                    // so Testcontainers can start PostgreSQL for security integration tests.
                    sh 'mvn -B clean test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'backend/target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        failure {
            echo 'Backend tests failed. Check Surefire reports and console output.'
        }
        success {
            echo 'Backend unit + security integration tests passed.'
        }
    }
}

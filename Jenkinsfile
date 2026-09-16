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
                    // Requires Docker on the Jenkins agent (socket or DinD) for Testcontainers PostgreSQL.
                    // CinemaSystemTest (Selenium) is excluded here — it needs frontend :3000 + Chrome.
                    // Run Selenium locally: mvn -B test -Dtest=CinemaSystemTest -Dselenium.headless=true
                    sh "mvn -B clean test -Dtest='!CinemaSystemTest'"
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
            echo 'Backend unit, validation, and security integration tests passed.'
        }
    }
}

pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {

        // ── Étape 1 : récupérer le code source ──────────────────────
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/houyemjb/my-project-sonar.git'
            }
        }

        // ── Étape 2 : compiler le projet Maven ───────────────────────
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // ── Étape 3 : analyse SonarQube via Maven ────────────────────
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.login=${SONAR_AUTH_TOKEN}'
                }
            }
        }

        // ── Étape 4 : attendre le résultat du Quality Gate ───────────
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ── Étape 5 : confirmation (atteinte si QG PASSED) ───────────
        stage('Build Success') {
            steps {
                echo 'Code validé par SonarQube – prêt pour Nexus'
            }
        }
    }

    post {
        failure {
            echo 'Pipeline arrêté – Quality Gate FAILED'
        }
    }
}

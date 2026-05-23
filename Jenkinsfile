pipeline {
    agent any

    tools {
        maven 'Maven-3'
    }

    environment {
        SONAR_HOST_URL = 'http://192.168.56.20:9000'
        NEXUS_URL = 'http://localhost:8081/repository/projet_nexus_sonar/'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/islemkortas/projetCI_CD_nexus_SonarQube_jenkins.git'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend/my-project-sonar') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('SonarQube Analysis') {
    when {
        expression { env.JOB_NAME == 'CI-CD-Pipeline' }
    }
    environment {
        SONAR_TOKEN = credentials('sonarqube_token')
    }
    steps {
        withSonarQubeEnv('SonarQube') {
            dir('backend/my-project-sonar') {
                sh 'mvn test'
                sh '''
                    mvn sonar:sonar \
                      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                '''
            }
        }
    }
}
        

        stage('Quality Gate') {
            when {
                expression { env.JOB_NAME == 'CI-CD-Pipeline' }
            }
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Create Tag') {
            when {
                expression { env.JOB_NAME == 'CI-CD-Pipeline' }
            }
            steps {
                script {
                    def tagName = "quality-passed-${env.BUILD_NUMBER}"
                    dir('backend/my-project-sonar') {
                        sh """
                            git tag ${tagName}
                            git push origin ${tagName}
                        """
                    }
                    echo "Tag cree: ${tagName}"
                }
            }
        }

        stage('Package Backend') {
            steps {
                dir('backend/my-project-sonar') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Publish to Nexus') {
            when {
                expression { env.JOB_NAME == 'nexus-publisher' }
            }
            environment {
                NEXUS_CREDS = credentials('nexus-credentials')
            }
            steps {
                script {
                    sh """
                        cd backend/my-project-sonar
                        JAR_FILE=\$(find target -name "*.jar" -type f | head -1)
                        FILENAME="backend-app-\${JOB_BASE_NAME}-\${BUILD_NUMBER}.jar"
                        
                        curl -v -u ${NEXUS_CREDS_USR}:${NEXUS_CREDS_PSW} \
                        --upload-file \${JAR_FILE} \
                        ${NEXUS_URL}\${FILENAME}
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline reussi'
        }
        failure {
            echo 'Pipeline echoue'
        }
        always {
            cleanWs()
        }
    }
}

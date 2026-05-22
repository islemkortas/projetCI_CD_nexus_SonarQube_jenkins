pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')
        NEXUS_CREDS = credentials('nexus-credentials')
        SONAR_HOST_URL = 'http://IP_DE_A:9000'
        NEXUS_URL = 'http://IP_DE_B:8081/repository/projet_nexus_sonar/'
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
                expression { env.JENKINS_URL.contains('8080') || env.SONAR_TOKEN != null }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    dir('backend/my-project-sonar') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            when {
                expression { env.JENKINS_URL.contains('8080') || env.SONAR_TOKEN != null }
            }
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
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
                expression { env.JENKINS_URL.contains('8081') || env.NEXUS_CREDS != null }
            }
            steps {
                script {
                    def jarFile = findFiles(glob: 'backend/my-project-sonar/target/*.jar')[0].path
                    def tagName = env.TAG_NAME ?: "snapshot-${env.BUILD_NUMBER}"
                    def fileName = "backend-app-${tagName}.jar"
                    
                    sh """
                        curl -v -u ${NEXUS_CREDS_USR}:${NEXUS_CREDS_PSW} \
                        --upload-file ${jarFile} \
                        ${NEXUS_URL}${fileName}
                    """
                }
            }
        }
    }

    post {
        success {
            script {
                if (env.JENKINS_URL.contains('8080') || env.SONAR_TOKEN != null) {
                    def tagName = "quality-passed-${env.BUILD_NUMBER}"
                    dir('backend/my-project-sonar') {
                        sh """
                            git tag ${tagName}
                            git push origin ${tagName}
                        """
                    }
                    echo "Tag créé: ${tagName}"
                }
                echo 'Pipeline réussi !'
            }
        }
        failure {
            echo 'Pipeline échoué !'
        }
        always {
            cleanWs()
        }
    }
}

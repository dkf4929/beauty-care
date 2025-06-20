pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/dkf4929/beauty-care.git'
        GIT_CREDENTIALS_ID = 'git-token'
        SSH_KEY_ID = 'ec2-ssh-key'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {
        stage('환경 변수 불러오기') {
            steps {
                withCredentials([
                    string(credentialsId: 'EC2_HOST', variable: 'EC2_HOST'),
                    string(credentialsId: 'EC2_USER', variable: 'EC2_USER'),
                    string(credentialsId: 'DEPLOY_DIR', variable: 'DEPLOY_DIR')
                ]) {
                    script {
                        env.EC2_HOST = "${EC2_HOST}"
                        env.EC2_USER = "${EC2_USER}"
                        env.DEPLOY_DIR = "${DEPLOY_DIR}"
                    }
                }
            }
        }

        stage('메인 브랜치 체크아웃') {
            steps {
                withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh """
                        rm -rf beauty-care
                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/dkf4929/beauty-care.git beauty-care
                        cd beauty-care
                        git checkout main
                    """
                }
            }
        }

        stage('Gradle Build') {
            steps {
                sh """
                    cd beauty-care
                    chmod +x ./gradlew
                    ./gradlew clean build
                """
            }
        }

        stage('JAR 파일 배포') {
            steps {
                sshagent([SSH_KEY_ID]) {
                    sh """
                        scp -o StrictHostKeyChecking=no beauty-care/build/libs/beauty-care-0.0.1-SNAPSHOT.jar ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/app.jar
                    """
                }
            }
        }

        stage('EC2에서 컨테이너 실행') {
            steps {
                sshagent([SSH_KEY_ID]) {
                    sh "scp -o StrictHostKeyChecking=no beauty-care/docker-compose.yml ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"
                    sh "scp -o StrictHostKeyChecking=no beauty-care/Dockerfile ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"

                    def dockerDeployScript = """
                        #!/bin/bash
                        set -e
                        docker-compose -f ${DEPLOY_DIR}/docker-compose.yml down
                        cd ${DEPLOY_DIR}
                        docker-compose up -d --build
                        docker image prune -af
                    """
                    sh "echo \"${dockerDeployScript}\" | ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST}"
                }
            }
        }
    }

    post {
        success {
            echo '🎉 배포 성공'
        }
        failure {
            echo '🚨 배포 실패'
        }
    }
}

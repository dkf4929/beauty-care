pipeline {
    agent any

    triggers {
        githubPush() // main push
    }

    environment {
        EC2_USER = 'ubuntu'
        EC2_HOST = '52.79.55.156'
        SSH_KEY_ID = 'ec2-ssh-key'
        DEPLOY_DIR = '/home/ubuntu/beauty-care/app'
        GIT_REPO = 'https://github.com/dkf4929/beauty-care.git'
        GIT_CREDENTIALS_ID = 'git-token'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    stages {
        stage('메인 브렌치 체크아웃') {
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
                script {
                    sh """
                        cd beauty-care
                        chmod +x ./gradlew
                        ./gradlew clean build
                    """
                }
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
                    script {
                        sh "scp -o StrictHostKeyChecking=no beauty-care/docker-compose.yml ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"
                        sh "scp -o StrictHostKeyChecking=no beauty-care/Dockerfile ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"

                        def dockerDeployScript = """#!/bin/bash
                                                    set -e  # 실패 시 즉시 종료
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
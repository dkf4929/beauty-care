pipeline {
    agent any

    triggers {
        githubPush() // main push
    }

    tools {
        jdk 'jdk-21'
    }

    environment {
        EC2_USER = 'ubuntu'
        EC2_HOST = '52.79.55.156'
        SSH_KEY_ID = 'ec2-ssh-key'
        DEPLOY_DIR = '/home/ubuntu/beauty-care/app'
        GIT_REPO = 'https://github.com/dkf4929/beauty-care.git'
        GIT_CREDENTIALS_ID = 'git-token'
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('메인 브렌치 체크아웃') {
            steps {
                withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh """
                        echo GIT_PASSWORD > git_password.txt
                        cat git_password.txt
                        rm -rf beauty-care  # 기존 배포 디렉토리를 삭제한다.
                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/dkf4929/beauty-care.git beauty-care
                        cd beauty-care
                        git checkout main
                    """
                }
            }
        }

        stage('도커 빌드') {
            steps {
                sh 'cd beauty-care && docker build -t beauty-care-app .'
            }
        }

        stage('ec2에 docker container 실행') {
            steps {
                sshagent([SSH_KEY_ID]) {
                    script {
                        sh "scp -o StrictHostKeyChecking=no beauty-care/docker-compose.yml ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"

                        // EC2에서 docker-compose로 Gradle 빌드를 실행하고 앱을 실행
                        def dockerDeployScript = """#!/bin/bash
                                                    cd ${DEPLOY_DIR}
                                                    docker-compose up --build --force-recreate -d
                                                    exit 0
                                                """

                        sh "echo \"${dockerDeployScript}\" | ssh -i ${SSH_KEY_PATH} -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo '배포 성공'
        }
        failure {
            echo '배포 실패'
        }
    }
}

// pipeline {
//     agent any
//
//     triggers {
//         githubPush() // main push
//     }
//
//     tools {
//         jdk 'jdk-21'
//     }
//
//     environment {
//         EC2_USER = 'ubuntu'
//         EC2_HOST = '52.79.55.156'
//         SSH_KEY_ID = 'ec2-ssh-key'
//         DEPLOY_DIR = '/home/ubuntu/beauty-care/app'
//         GIT_REPO = 'https://github.com/dkf4929/beauty-care.git'
//         GIT_CREDENTIALS_ID = 'git-token'
//         DOCKER_COMPOSE_FILE = 'docker-compose.yml'
//         DOCKER_HOST = 'unix:///var/run/docker.sock'
//     }
//
//     stages {
//         stage('메인 브렌치 체크아웃') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
//                     sh """
//                         rm -rf beauty-care  # 기존 배포 디렉토리를 삭제한다.
//                         git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/dkf4929/beauty-care.git beauty-care
//                         cd beauty-care
//                         git checkout main
//                     """
//                 }
//             }
//         }
//
//         stage('접속한 사용자 확인') {
//             steps {
//                 script {
//                     // whoami를 사용하여 접속한 사용자 확인
//                     def user = sh(script: 'whoami', returnStdout: true).trim()
//                     echo "현재 접속한 사용자: ${user}"
//
//                     // id 명령어로 사용자 그룹 확인
//                     def userGroups = sh(script: 'id', returnStdout: true).trim()
//                     echo "사용자 그룹: ${userGroups}"
//                 }
//             }
//         }
//
//         stage('도커 환경 확인') {
//             steps {
//                 script {
//                     // /var/run/docker.sock에 접근할 수 있는지 확인
//                     def dockerSocketStatus = sh(script: 'ls -l /var/run/docker.sock', returnStdout: true).trim()
//                     echo "Docker socket 상태: ${dockerSocketStatus}"
//
//                     // Docker 정보 확인 (Docker가 제대로 실행되고 있는지 확인)
//                     def dockerInfo = sh(script: 'docker info', returnStdout: true).trim()
//                     echo "Docker 정보: ${dockerInfo}"
//                 }
//             }
//         }
//
//         stage('도커 빌드') {
//                 steps {
//                     sh 'cd beauty-care && docker build -t beauty-care-app .'
//             }
//         }
//
//         stage('도커 컨테이너 실행 및 빌드') {
//             steps {
//                 sshagent([SSH_KEY_ID]) {
//                     script {
//                         sh "scp -o StrictHostKeyChecking=no beauty-care/docker-compose.yml ${EC2_USER}@${EC2_HOST}:${DEPLOY_DIR}/"
//
//                         // EC2에서 Docker Compose로 애플리케이션 실행
//                         def dockerDeployScript = """#!/bin/bash
//                                                     docker-compose -f ${DEPLOY_DIR}/docker-compose.yml down || true
//                                                     cd ${DEPLOY_DIR}
//                                                     docker-compose up -d
//                                                     exit 0
//                                                 """
//                         sh "echo \"${dockerDeployScript}\" | ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST}"
//                     }
//                 }
//             }
//         }
//     }
//
//     post {
//         success {
//             echo '배포 성공'
//         }
//         failure {
//             echo '배포 실패'
//         }
//     }
// }
pipeline {
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
        DOCKER_HOST = 'unix:///var/run/docker.sock'
    }

    agent {
        docker {
            image 'gradle:8.12.1-jdk21'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('메인 브렌치 체크아웃') {
            steps {
                withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh """
                        rm -rf beauty-care  # 기존 배포 디렉토리를 삭제한다.
                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/dkf4929/beauty-care.git beauty-care
                        cd beauty-care
                        git checkout main
                    """
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh """
                        cd beauty-care
                        chmod +x ./gradlew
                        ./gradlew clean build -i
                    """
                }
            }
        }
    }
}

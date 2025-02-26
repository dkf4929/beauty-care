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
        DB_HOST = '52.79.55.156'
        DB_PORT = '3306'
        DB_USER = 'root'
        DB_PASSWORD = 'qwer1234'
        DB_NAME = 'beauty_care'
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

        stage('데이터베이스 연결 테스트') {
                    steps {
                        script {
                            // 데이터베이스 연결 테스트 스크립트 실행
                            sh '''
                            #!/bin/bash
                            echo "데이터베이스 연결 테스트를 시작합니다..."
                            mysql -h ${DB_HOST} -P ${DB_PORT} -u ${DB_USER} -p${DB_PASSWORD} -e "USE ${DB_NAME}; SELECT 1;"
                            if [ $? -ne 0 ]; then
                                echo "데이터베이스에 연결할 수 없습니다."
                                exit 1
                            fi
                            echo "데이터베이스 연결에 성공했습니다."
                            '''
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

                        // EC2에서 Docker Compose로 애플리케이션 실행
                        def dockerDeployScript = """#!/bin/bash
                            docker-compose -f ${DEPLOY_DIR}/docker-compose.yml down || true
                            cd ${DEPLOY_DIR}
                            docker-compose up -d
                            exit 0
                        """
                        sh "echo \"${dockerDeployScript}\" | ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST}"
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
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
                sh 'docker build -t chatfia .'
            }
        }
        stage('Deploy to Blue') {
            steps {
                script {
                    // Blue 환경에 배포
                    sh 'docker-compose up -d chatfia-blue'
                }
            }
        }
        stage('Switch Traffic to Blue') {
            steps {
                script {
                    // Nginx 설정을 업데이트하여 트래픽을 Blue 환경으로 전환
                    sh """
                    sudo sed -i 's/proxy_pass http:\\/\\/green;/proxy_pass http:\\/\\/blue;/' /etc/nginx/nginx.conf
                    sudo systemctl reload nginx
                    """
                }
            }
        }
        stage('Deploy to Green') {
            steps {
                script {
                    // Green 환경에 배포
                    sh 'docker-compose up -d chatfia-green'
                }
            }
        }
    }
}

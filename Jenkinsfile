pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'echo "Building the application..."'
                sh './gradlew build'
                sh 'echo "Building Docker image..."'
                sh 'docker build -t chatfia_image .'
            }
        }
        stage('Deploy to Blue') {
            steps {
                script {
                    // Blue 환경에 배포
                    sh 'echo "Deploying to Blue environment..."'
                    sh 'docker-compose up -d chatfia-blue'
                }
            }
        }
        stage('Switch Traffic to Blue') {
            steps {
                script {
                    // Nginx 설정을 업데이트하여 트래픽을 Blue 환경으로 전환
                    sh 'echo "Switching traffic to Blue environment..."'
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
                    sh 'echo "Deploying to Green environment..."'
                    sh 'docker-compose up -d chatfia-green'
                }
            }
        }
    }
}


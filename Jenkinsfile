node {

    stage('check tools') {
        sh "node -v"
        sh "npm -v"
        sh "bower -v"
        sh "gulp -v"
    }

    stage('checkout') {
        checkout scm
    }

    stage('npm install') {
        sh "npm install"
    }

    stage('clean') {
        sh "./mvnw clean"
    }

    stage('backend tests') {
        sh "./mvnw test"
    }

    stage('frontend tests') {
        sh "gulp test"
    }

    stage('packaging') {
        sh "./mvnw package -Pprod -DskipTests"
    }
}

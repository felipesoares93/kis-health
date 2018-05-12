#!/usr/bin/env groovy

node {

    stage('checkout') {
        checkout scm
    }

    stage('check tools') {
        sh "node -v"
        sh "npm -v"
        sh "java -version"
    }

    stage('npm install') {
        sh "npm install"
    }

    stage('clean') {
//        sh "chmod +x mvnw"
//        sh "./mvnw clean"
        sh "mvn clean"
    }

    stage('backend tests') {
//        sh "./mvnw test"
//        sh "mvn test"
    }

//    stage('frontend tests') {
//        sh "gulp test"
//    }

    stage('packaging') {
//        sh "./mvnw package -Pprod -DskipTests"
        sh "mvn package -Pdev -DskipTests"
        archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
    }
}


node('linux') {
  stage ('Poll') {
    checkout([
      $class: 'GitSCM', branches: [[name: '*/main']], extensions: [],
      userRemoteConfigs: [[url: 'https://github.com/zopencommunity/utf8procport.git']]])
  }
  stage('Build') {
    build job: 'Port-Pipeline', parameters: [
      string(name: 'PORT_GITHUB_REPO', value: 'https://github.com/zopencommunity/utf8procport.git'),
      string(name: 'PORT_DESCRIPTION', value: 'A clean C library for processing UTF-8 Unicode data'),
      string(name: 'BUILD_LINE', value: 'STABLE')
    ]
  }
}

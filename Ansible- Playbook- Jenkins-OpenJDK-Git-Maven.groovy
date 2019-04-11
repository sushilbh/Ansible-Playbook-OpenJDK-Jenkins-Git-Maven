#The complete jenkins package

---
- name: installing git
  hosts: all
  become: true
  tasks:

    - block:
      - name: git installation
        yum:
          name: git
          state: present
      tags:
        - git



- name: jenkins project
  hosts: master
  become: true
  tasks:

    - block:
      - name: installing java
        yum:
          name: java
          state: present

      - name: downloading jenins
        get_url:
          url: http://pkg.jenkins-ci.org/redhat-stable/jenkins.repo
          dest: /etc/yum.repos.d/jenkins.repo
      - name: importing key for jenkins installation
        rpm_key:
          key: https://jenkins-ci.org/redhat/jenkins-ci.org.key
          state: present
      - name: installing jenkins
        yum:
          name: jenkins
          state: present
      - name: starting jenkins
        service:
          name: jenkins
          state: restarted
      tags:
        - jenkins



- name: openjdk installation in agent
  hosts: agent target
  become: true
  tasks:

    - block:
      - name: copying the jdk file to target machine
        copy:
          src: /opt/jdk-8u201-linux-x64.tar.gz
          dest: /opt/jdk-8u201-linux-x64.tar.gz
          mode: 0644

      - name: untarring the tar file on target
        shell: "tar -xvzf /opt/jdk-8u201-linux-x64.tar.gz"

      - name: installing java
        shell: "alternatives --install /usr/bin/java java /opt/jdk1.8.0_201/bin/java 1"

      - name: java version selection using alternatives
        alternatives:
          name: java
          path: /opt/jdk1.8.0_201/bin/java
      tags:
        - jdk



- name: maven installation in agent
  hosts: agent
  become: true
  tasks:


    - block:
        - name: installing maven tool in target machine
          yum:
            name: maven
            state: present
        - name: downloading maven
          get_url:
            url: http://mirror.cc.columbia.edu/pub/software/apache/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz
            dest: /opt
        - name: untarring maven
          shell: chdir=/opt creates=/opt/apache-maven-3.6.0 tar -zxf apache-maven-3.6.0-bin.tar.gz -C .
        - name: giving path to maven home
          shell: export PATH=$PATH:/opt/apache-maven-3.6.0/bin
        - name: updating .bashrc
          shell: . /home/ec2-user/.bashrc && export PATH=$PATH:/opt/apache-maven-3.6.0/bin
      tags:
        - maven
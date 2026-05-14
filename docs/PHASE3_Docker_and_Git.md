# PHASE 3 — Docker & Version Control
## Smart Logistics & Transportation Management System
### Impano Gateway Logistics Ltd

---

## Part A: Dockerizing the Application

### What is Docker?
Docker is a platform that packages an application and all its dependencies into a
lightweight, portable container. The container runs identically on any machine —
developer laptop, test server, or production cloud — eliminating "it works on my
machine" problems.

### Process to Dockerize an Application

**Step 1 — Install Docker**
Download and install Docker Desktop from https://www.docker.com/products/docker-desktop

**Step 2 — Write a Dockerfile**
The Dockerfile is a script that tells Docker how to build the container image.
Our Dockerfile uses a two-stage build:
- Stage 1 (builder): Uses Maven to compile and package the Java application into a JAR
- Stage 2 (runtime): Uses a lightweight JRE image to run the JAR

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/smart-logistics-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Step 3 — Build the Docker Image**
```bash
cd Smart_Logistics
docker build -t impano-logistics:1.0 .
```

**Step 4 — Run the Container**
Since this is a console application (interactive), run with -it flag:
```bash
docker run -it impano-logistics:1.0
```

**Step 5 — Verify the Container**
```bash
docker images                        # list images
docker ps                            # list running containers
docker logs <container_id>           # view logs
```

**Step 6 — Stop and Remove**
```bash
docker stop <container_id>
docker rm <container_id>
docker rmi impano-logistics:1.0
```

### Benefits of Dockerizing SLTMS
- Consistent environment across development, testing, and production
- Easy deployment to any server or cloud (AWS ECS, Azure, GCP)
- Isolation from other applications on the same machine
- Simple version management of the application

---

## Part B: Version Control with Git

### What is Git?
Git is a distributed Version Control System (VCS) that tracks changes to source code,
allows multiple developers to collaborate, and enables reverting to previous versions.

### Installation & Configuration

**Step 1 — Install Git**
Download from https://git-scm.com/downloads and install.

**Step 2 — Configure Git Identity**
```bash
git config --global user.name "https://github.com/hirwa333/Best_Programming_Final_Project"
git config --global user.email "your.email@impano.rw"
```

**Step 3 — Initialize Repository**
```bash
cd Smart_Logistics
git init
```

**Step 4 — Add .gitignore**
The .gitignore file excludes compiled files, IDE files, and logs:
```
target/
*.class
*.jar
*.log
.idea/
```

**Step 5 — Stage and Commit All Files**
```bash
git add .
git commit -m "Initial commit: Smart Logistics & Transportation Management System v1.0"
```

**Step 6 — Create a Remote Repository (GitHub)**
```bash
git remote add origin https://github.com/your-username/smart-logistics.git
git branch -M main
git push -u origin main
```

**Step 7 — Ongoing Development Workflow**
```bash
# Create a feature branch
git checkout -b feature/add-route-management

# Make changes, then stage and commit
git add .
git commit -m "feat: add route management module"

# Push branch and create pull request
git push origin feature/add-route-management

# After review, merge to main
git checkout main
git merge feature/add-route-management
git push origin main
```

### Git Branching Strategy for SLTMS

```
main          ──────────────────────────────────> (production-ready)
                  \              /
develop        ────────────────────────────────> (integration)
                  \    /    \    /
feature/*      ────    ──────    ──────────────> (individual features)
```

| Branch | Purpose |
|--------|---------|
| main | Stable, production-ready code |
| develop | Integration branch for features |
| feature/shipment | Shipment module development |
| feature/invoicing | Invoice module development |
| hotfix/* | Emergency bug fixes |

### Key Git Commands Reference

| Command | Description |
|---------|-------------|
| `git status` | Show changed files |
| `git log --oneline` | View commit history |
| `git diff` | Show unstaged changes |
| `git stash` | Temporarily save changes |
| `git revert <hash>` | Undo a specific commit |
| `git tag v1.0` | Tag a release version |

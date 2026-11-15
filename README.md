# 1. Launch DB on port 5433
docker compose up -d

# 2. Run application
mvn clean compile exec:java

# 3. Stop
docker compose down          # keep data
docker compose down -v       # delete data
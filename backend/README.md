# GEHU Smart Women Safety - Backend (Stage 1: Foundation)

## What's in this stage
- Full MySQL schema for the whole system (`database/schema.sql`)
- Auth module: register, login, JWT access + refresh tokens, logout
- User profile module: `/api/users/me`
- Trusted Contacts module: full CRUD, scoped to the logged-in user
- Spring Security with role-based access control (USER, MODERATOR, SECURITY_STAFF, ADMIN)
- Centralized exception handling and request validation

## Setup
1. Create the MySQL database by running `database/schema.sql` in MySQL Workbench / CLI,
   OR let Hibernate auto-create tables (`ddl-auto: update` is already set in `application.yml`)
   — running the SQL script yourself is recommended so you fully understand the schema.
2. Edit `src/main/resources/application.yml`:
   - Set your real MySQL username/password
   - Replace `app.jwt.secret` with your own long random string (never commit the real one)
3. Run: `mvn spring-boot:run`
4. API base URL: `http://localhost:8080`

## Quick test flow (Postman/curl)
1. `POST /api/auth/register` → get accessToken + refreshToken
2. `POST /api/auth/login` → same
3. Use `Authorization: Bearer <accessToken>` header for all protected endpoints
4. `GET /api/users/me` → your own profile
5. `POST /api/contacts` → add a trusted contact
6. `GET /api/contacts` → list your trusted contacts

See the project chat history / SRS document for the full module roadmap.

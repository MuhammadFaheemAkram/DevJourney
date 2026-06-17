# Security Policy

DevJourney is a demo learning app with no real backend and no production user accounts. Security issues are still taken seriously because the project demonstrates Android engineering practices.

## Supported Versions

The `main` branch is the only actively supported development line.

## Reporting A Vulnerability

Please do not open public issues for sensitive vulnerabilities. Instead, contact the maintainers privately through the repository owner's preferred contact channel.

Include:

- A concise description of the issue.
- Steps to reproduce.
- Impact and affected files or features.
- Suggested fix, if known.

## Scope

In scope:

- Local data handling problems.
- Unsafe dependency or build configuration.
- Insecure examples that could mislead learners.
- WorkManager, DataStore, or Room misuse with security implications.

Out of scope:

- Fake backend availability.
- Demo data content disputes.
- Issues requiring a rooted or compromised device unless the impact is educationally important.

## Response Expectations

Maintainers should acknowledge reports within a reasonable time, investigate with care, and document the fix in the changelog when disclosure is safe.

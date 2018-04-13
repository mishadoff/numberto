# Deployment to Clojars

1. Install GPG
2. Create a key (or use existing one)
3. (Optional) Hack if error with gpg `export GPG_TTY=$(tty)`
4. `lein deploy clojars`
# Important business-cases for testing


## Hackernews
- Parser
    - Given project on Codexia
    - When new Hackernews link appears, new codexia-review should be created
    
- When Hackernews post become deleted
    - We should not send review to Codexia
    - We should mark corresponding github_repo_source as deleted

## Reddit
- Parser
    - Given project on Codexia
    - When new Reddit link appears, new codexia-review should be created


## Github
- Add new project with Codexia source
- Add new project with Hackernews source
- Add new project with Reddit source


## Codexia
- Resend erroneous reviews to Codexia
- When new Codexia-project is published it appears in DB
    - With corresponding github-project
    - Without corresponding github-project (if nothing found on GitHub)

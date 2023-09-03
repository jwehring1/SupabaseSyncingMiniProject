# SupabaseSyncingMiniProject

## Introduction

Primary objective was to create a small-scale application designed to showcase the seamless synchronization of a local database using Room/SQLDelight with a remote database managed by Supabase. Additionally, the project aimed to leverage the WorkManager to facilitate efficient and automated data synchronization while adhering to the clean architecture Use Case pattern

## Table of Contents

- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Getting Started

Follow these steps to set up the project:

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/jwehring1/SupabaseSyncingMiniProject

2. **Configuring Supabase:**  
   1. Create a free tier Supabase Account on supabase.com
   2. Create a Supabase Table  
      In your Supabase dashboard, create a new table named "users" with the following columns:  

      - uuid as type uuid  
      - first_name as type text  
      - last_name as type text  
      - email as type text  
      - updated_at as type timestamptz with default to now()   
   3. Navigate to SQL Editor and create a new query for initial users
  ```bash
INSERT INTO users (first_name, last_name, email, updated_at) 
VALUES
    ('John', 'Doe', 'john@example.com', NOW()),
    ('Jane', 'Smith', 'jane@example.com', NOW()),
    ('Michael', 'Johnson', 'michael@example.com', NOW()),
    ('Emily', 'Williams', 'emily@example.com', NOW()),
    ('William', 'Brown', 'william@example.com', NOW()),
    ('Olivia', 'Jones', 'olivia@example.com', NOW()),
    ('James', 'Taylor', 'james@example.com', NOW()),
    ('Sophia', 'Martinez', 'sophia@example.com', NOW()),
    ('Robert', 'Anderson', 'robert@example.com', NOW()),
    ('Emma', 'Hernandez', 'emma@example.com', NOW()),
    ('David', 'Garcia', 'david@example.com', NOW()),
    ('Ava', 'Lopez', 'ava@example.com', NOW()),
    ('Joseph', 'Wilson', 'joseph@example.com', NOW()),
    ('Samantha', 'Martin', 'samantha@example.com', NOW()),
    ('Charles', 'Miller', 'charles@example.com', NOW()),
    ('Madison', 'Davis', 'madison@example.com', NOW()),
    ('Daniel', 'Rodriguez', 'daniel@example.com', NOW()),
    ('Lily', 'Perez', 'lily@example.com', NOW()),
    ('Matthew', 'Gonzalez', 'matthew@example.com', NOW()),
    ('Chloe', 'Walker', 'chloe@example.com', NOW()),
    ('Andrew', 'White', 'andrew@example.com', NOW());
```
  
  3. Obtain Your Supabase API Key and URL:  
    Visit your Supabase project settings to find your API key and URL.  

  4. Define Configuration in local.properties:
    Create a local.properties file in your project directory if it doesn't exist. Add the following lines:
     ```bash
     SUPABASE_ANON_KEY=your-supabase-api-key
     SUPABASE_URL=your-supabase-url
     ```

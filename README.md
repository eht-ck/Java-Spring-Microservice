# Java Spring Microservices Project

This project consists of three microservices:
1. **User Service**
2. **Demo (Product) Service**
3. **Purchase Service**

## Prerequisites

- Java 17 
- Maven
- Ngrok (or any other tool to expose localhost)

## Getting Started

### Running the Microservices

1. **Clone the repository:**

    ```bash
   https://github.com/eht-ck/Java-Spring-Microservice
    cd Java-Spring-Microservice
    ```

2. **Build the project:**

    ```bash
    mvn clean install
    ```

3. **Run the User Service:**

    ```bash
    cd user
    mvn spring-boot:run
    ```

4. **Run the Demo (Product) Service:**

    ```bash
    cd ../demo
    mvn spring-boot:run
    ```

5. **Run the Purchase Service:**

    ```bash
    cd ../purchase
    mvn spring-boot:run
    ```

### Exposing Purchase Service to Public

To use the Purchase Service with Stripe, you need to expose it to the public internet. You can use Ngrok for this purpose.

1. **Download and install Ngrok:**

    ```bash
    # For Windows
    choco install ngrok

    # For MacOS
    brew install ngrok

    # For Linux
    sudo apt-get install ngrok
    ```

2. **Expose the Purchase Service:**

    ```bash
    ngrok http 8082
    ```

    This will give you a public URL like `http://<ngrok-id>.ngrok.io`.

### Configuring Stripe Webhook

1. **Log in to your Stripe Dashboard.**
2. **Navigate to Developers > Webhooks.**
3. **Click on "Add endpoint" and enter the Ngrok URL:**

    ```plaintext
    http://<ngrok-id>.ngrok.io/webhook
    ```

4. **Select the events you want to listen to and save the endpoint.**

## Microservices Details

### User Service

- **Port:** 8080
- **Description:** Manages user information and authentication.

### Demo (Product) Service

- **Port:** 8081
- **Description:** Manages product information and inventory.

### Purchase Service

- **Port:** 8082
- **Description:** Handles purchase transactions and integrates with Stripe for payment processing.

# Pro Scanner

## Overview
**Pro Scanner** is a powerful and user-friendly document scanner app built using Android with Jetpack Compose. The app follows the MVVM (Model-View-ViewModel) architecture to ensure a clean and maintainable codebase. Pro Scanner allows users to scan documents, save them as PDF files or images, and organize them efficiently.

## Features
- **Scan Documents**: Easily scan documents and save them in both internal and external storage with appropriate permissions.
- **Swipe to Delete**: Users can delete PDF files with a simple swipe gesture.
- **Save as Images**: Save PDF pages as images in external storage for easy access and sharing.
- **Manage PDF Files**: Rename, share, and delete PDF files directly within the app.
- **Auto Scan and Edge Detection**: Utilize ML Kit's document scanner library for automatic scanning and edge detection.
- **Categorize Files**: Organize scanned files by selecting categories, which internally create directories for better management.

## Technology
- **Android with Jetpack Compose**: Utilizes modern Android development tools to create a sleek and responsive UI.
- **MVVM Architecture**: Ensures a modular, testable, and maintainable code structure.

## Code Structure
The app is built using the MVVM pattern, which separates concerns and provides a clear structure:
- **Model**: Handles data operations and business logic.
- **View**: Comprises the UI elements created using Jetpack Compose.
- **ViewModel**: Bridges the Model and View, managing UI-related data and handling user interactions.

### Directory Structure
- When a user creates a category and saves a file in it, a directory with the same name is created internally to store the PDF file.
- Saving images of PDF pages creates another external directory for easy access and organization.

## Screenshots

| Home screen | Rename File and/or change category | Multiple select and funtionality |
|-------------|-------------|-------------|
| ![1](https://github.com/user-attachments/assets/b24c99c1-2d70-4d03-b5b4-4ac2bfe42135) | ![2](https://github.com/user-attachments/assets/9a0cec84-4d1c-44d1-a059-c97b19fa4ca2) | ![3](https://github.com/user-attachments/assets/8261b53d-b1fe-44e3-8574-9eaad9fd93d8) |

| Search file | Scan file and/or choose form gallery | Manage settings of App |
|-------------|-------------|-------------|
| ![4](https://github.com/user-attachments/assets/d9f2c650-62b4-42a7-87a8-ac74a4270baa) | ![5](https://github.com/user-attachments/assets/26302d28-b486-4aa2-a24d-bf93fd5a9ec2) | ![6](https://github.com/user-attachments/assets/d07970eb-0ab8-4a88-a593-977f5f47d1d5) |

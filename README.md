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
| ![1](https://github.com/user-attachments/assets/6a5b1b00-7f7f-479f-8e7d-1a7d175ae42b) | ![2](https://github.com/user-attachments/assets/a6305fb2-8e57-4373-b316-762ba530fc63) | ![3](https://github.com/user-attachments/assets/2e739c44-213c-483f-a313-1c95198d5fe0) |

| Search file | Scan file and/or choose form gallery | Manage settings of App |
|-------------|-------------|-------------|
| ![4](https://github.com/user-attachments/assets/7c294d57-6f37-418b-855a-9ccc27afd368) | ![5](https://github.com/user-attachments/assets/5205e9ca-2e5a-4234-a22e-da21d4b4f29b) | ![6](https://github.com/user-attachments/assets/b3038d95-88c4-4d4e-9139-b59ea3d5f48f) |

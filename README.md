# Pro Scanner

## Overview
**Pro Scanner** is a powerful and user-friendly document scanner app built using Android with Jetpack Compose. The app follows the MVVM (Model-View-ViewModel) architecture to ensure a clean and maintainable codebase. Pro Scanner allows users to scan documents, save them as PDF files or images, and organize them efficiently.

## Live Demo

Get the Pro scanner app from Google Play: [https://play.google.com/store/apps/details?id=com.elite.scanner](https://play.google.com/store/apps/details?id=com.elite.scanner)

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

| Home screen | Rename and/or change category | Multiple select |
|-------------|-------------|-------------|
| ![1](https://github.com/user-attachments/assets/f9a4ecd2-8e38-4244-81f3-1451dc2e6564) | ![2](https://github.com/user-attachments/assets/1217b112-4f20-43ca-929d-e0801a74bd55)  | ![3](https://github.com/user-attachments/assets/d0e54b8b-a251-408c-a725-a3e997579c00) |


| Scan/upload doc | Retouch document | Manage settings |
|-------------|-------------|-------------|
| ![4](https://github.com/user-attachments/assets/f6263ed9-1df7-440f-b886-4b191991b650) | ![5](https://github.com/user-attachments/assets/f1a9db67-7cb3-46f4-a603-d21e7eb17a8c) | ![6](https://github.com/user-attachments/assets/d07970eb-0ab8-4a88-a593-977f5f47d1d5) |

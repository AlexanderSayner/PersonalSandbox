# Shop Web UI Service

A SvelteKit-based UI service for the bookshop application with a responsive design supporting desktop and mobile resolutions.

## Features

- **Responsive Design**: Adapts to different screen sizes from mobile to desktop
- **Product Catalog**: Displays products with pagination support
- **Category Filtering**: Sidebar with category navigation
- **Shopping Basket**: Header component with basket icon and counter
- **Orders Page**: View order history and status
- **Clean UI**: Modern, clean interface with proper spacing

## Project Structure

```
shop-web-ui-service/
├── package.json
├── svelte.config.js
├── vite.config.js
├── src/
│   ├── app.html
│   ├── app.css
│   └── routes/
│       ├── +layout.svelte
│       ├── +page.svelte (home page)
│       ├── products/
│       │   └── +page.svelte
│       └── orders/
│           └── +page.svelte
```

## Pages

- **Home Page**: Featured products display
- **Products Page**: Grid layout with pagination and category filtering
- **Orders Page**: Order history with status tracking

## Components

- **Header**: With logo, navigation links, login button and shopping basket
- **Sidebar**: Category navigation that supports URL parameters
- **Product Grid**: Displays up to 4 products per row with image, name, price, and description
- **Pagination**: Controls for navigating through product pages

## Responsive Design

- Mobile-first approach with media queries
- Flexible grid layout for products
- Collapsible sidebar on smaller screens
- Appropriate spacing and sizing for all device types

## How to Run

1. Install dependencies: `npm install`
2. Start development server: `npm run dev`
3. Open your browser to the displayed URL

## Mock Data

The application uses mock data for demonstration purposes. In a real implementation, this would connect to the GraphQL API from the bookshop service.
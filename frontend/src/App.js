import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './components/MainLayout';
import ProductTable from './components/ProductTable';
import OrderTable from './components/OrderTable';
import OrderCard from "./components/OrderCard";

function App() {
    return (
        <Router>
            <MainLayout>
                <Routes>
                    <Route path="/" element={<ProductTable />} />
                    <Route path="/orders" element={<OrderTable />} />
                    <Route path="/orders/:id" element={<OrderCard />} />
                </Routes>
            </MainLayout>
        </Router>
    );
}

export default App;




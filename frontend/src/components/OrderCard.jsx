import { useParams, useNavigate } from 'react-router-dom';
import { Card, List, Typography, Button, message, Select, Divider } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';

export default function OrderCard() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [products, setProducts] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState();

    const fetchOrder = () => {
        axios.get(`http://localhost:8080/orders/${id}`)
            .then(res => setOrder(res.data))
            .catch(() => message.error('Ошибка при загрузке заказа'));
    };

    const fetchProducts = () => {
        axios.get(`http://localhost:8080/products`)
            .then(res => setProducts(res.data))
            .catch(console.error);
    };

    useEffect(() => {
        fetchOrder();
        fetchProducts();
    }, []);

    const handleAdd = () => {
        if (!selectedProductId) return;
        axios.put(`http://localhost:8080/orders/${id}/add-product/${selectedProductId}`)
            .then(() => {
                message.success('Продукт добавлен');
                fetchOrder();
            })
            .catch(() => message.error('Ошибка добавления'));
    };

    const handleRemove = (productId) => {
        axios.put(`http://localhost:8080/orders/${id}/remove-product/${productId}`)
            .then(() => {
                message.success('Продукт удалён');
                fetchOrder();
            })
            .catch(() => message.error('Ошибка удаления'));
    };

    if (!order) return null;

    return (
        <div style={{ maxWidth: 600, margin: '40px auto' }}>
            <Card
                title={
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span>🧾 Заказ №{order.id} — <strong>{order.customerName}</strong></span>
                        <Button onClick={() => navigate(-1)}>← Назад</Button>
                    </div>
                }
                style={{
                    background: '#f7f7f7',
                    borderRadius: '12px',
                    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.05)',
                    padding: '20px',
                    border: '1px solid #dcdcdc',
                }}
            >
                <Typography.Text type="secondary">📅 Дата: {order.orderDate}</Typography.Text><br/>
                <Typography.Text type="secondary">💰 Сумма: {order.totalAmount} y.e.</Typography.Text>

                <Divider />

                <Typography.Title level={5} style={{ marginBottom: 10 }}>📦 Товары в заказе</Typography.Title>

                <List
                    dataSource={order.products}
                    renderItem={product => (
                        <List.Item
                            style={{
                                background: '#fff',
                                borderRadius: 6,
                                marginBottom: 8,
                                padding: '8px 16px',
                                border: '1px solid #eee',
                            }}
                            actions={[
                                <Button danger size="small" onClick={() => handleRemove(product.id)}>Удалить</Button>
                            ]}
                        >
                            {product.name} — {product.price} у.e.
                        </List.Item>
                    )}
                />

                <Divider />

                <Typography.Text style={{ display: 'block', marginBottom: 6 }}>
                    ➕ Добавить товар
                </Typography.Text>

                <Select
                    showSearch
                    placeholder="Выберите товар"
                    style={{ width: '100%', marginBottom: 12 }}
                    value={selectedProductId}
                    onChange={setSelectedProductId}
                    options={products.map(p => ({ label: p.name, value: p.id }))}
                />
                <Button
                    type="primary"
                    onClick={handleAdd}
                    style={{ backgroundColor: '#40e0d0', color: '#000', width: '100%' }}
                >
                    Добавить
                </Button>
            </Card>
        </div>
    );
}

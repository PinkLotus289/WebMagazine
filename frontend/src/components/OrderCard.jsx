import { useParams, useNavigate } from 'react-router-dom';
import { Card, List, Typography, Button, message, Select } from 'antd';
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
        <Card
            title={`Заказ №${order.id} (${order.customerName})`}
            style={{ background: '#1f1f1f', color: '#fff' }}
            extra={<Button onClick={() => navigate(-1)}>← Назад</Button>}
        >
            <Typography.Text style={{ color: '#ccc' }}>Дата: {order.orderDate}</Typography.Text><br/>
            <Typography.Text style={{ color: '#ccc' }}>Сумма: {order.totalAmount}</Typography.Text>

            <List
                header="Товары в заказе"
                dataSource={order.products}
                renderItem={product => (
                    <List.Item
                        style={{ background: '#141414', color: '#fff' }}
                        actions={[
                            <Button danger size="small" onClick={() => handleRemove(product.id)}>Удалить</Button>
                        ]}
                    >
                        {product.name} — {product.price}
                    </List.Item>
                )}
                style={{ marginTop: 16 }}
            />

            <div style={{ marginTop: 24 }}>
                <Select
                    showSearch
                    placeholder="Выберите товар"
                    style={{ width: '100%' }}
                    value={selectedProductId}
                    onChange={setSelectedProductId}
                    options={products.map(p => ({ label: p.name, value: p.id }))}
                />
                <Button
                    type="primary"
                    onClick={handleAdd}
                    style={{ marginTop: 8, backgroundColor: '#40e0d0', color: 'black' }}
                >
                    Добавить товар
                </Button>
            </div>
        </Card>
    );
}

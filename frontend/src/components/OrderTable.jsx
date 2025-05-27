import { Table, Button, Space, Modal, message } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
import OrderForm from './OrderForm';
import { useNavigate } from 'react-router-dom';

export default function OrderTable() {
    const [orders, setOrders] = useState([]);
    const [editingOrder, setEditingOrder] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [toDelete, setToDelete] = useState(null); // 👈 Для своей модалки
    const navigate = useNavigate();

    const fetchOrders = () => {
        axios.get('http://localhost:8080/orders')
            .then(res => setOrders(res.data))
            .catch(console.error);
    };

    useEffect(() => {
        fetchOrders();
    }, []);

    const columns = [
        { title: 'Имя клиента', dataIndex: 'customerName' },
        {
            title: 'Продукты',
            render: (_, record) => record.products.map(p => p.name).join(', ')
        },
        { title: 'Сумма', dataIndex: 'totalAmount' },
        { title: 'Дата', dataIndex: 'orderDate' },
        {
            title: 'Действия',
            render: (_, record) => (
                <Space>
                    <Button
                        style={{ color: '#40e0d0' }}
                        onClick={() => {
                            setEditingOrder(record);
                            setIsModalOpen(true);
                        }}
                    >
                        Изменить
                    </Button>
                    <Button danger onClick={() => setToDelete(record)}>Удалить</Button>
                </Space>
            )
        }
    ];

    return (
        <>
            <Button
                style={{ marginBottom: 16, backgroundColor: '#40e0d0', color: 'black' }}
                onClick={() => {
                    setEditingOrder(null);
                    setIsModalOpen(true);
                }}
            >
                Добавить заказ
            </Button>

            <Table
                dataSource={orders}
                columns={columns}
                rowKey="id"
                pagination={{ pageSize: 10 }}
                onRow={(record) => ({
                    onClick: () => navigate(`/orders/${record.id}`)
                })}
            />


            <OrderForm
                visible={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onSuccess={() => {
                    setIsModalOpen(false);
                    fetchOrders();
                }}
                order={editingOrder}
            />

            {/* 🔥 Модалка подтверждения удаления */}
            <Modal
                open={!!toDelete}
                title={`Удалить заказ клиента "${toDelete?.customerName}"?`}
                onCancel={() => setToDelete(null)}
                onOk={() => {
                    axios.delete(`http://localhost:8080/orders/${toDelete.id}`)
                        .then(() => {
                            message.success('Удалено');
                            fetchOrders();
                        })
                        .catch(() => message.error('Ошибка при удалении'))
                        .finally(() => setToDelete(null));
                }}
                okButtonProps={{ style: { backgroundColor: '#40e0d0', borderColor: '#40e0d0' } }}
                okText="Удалить"
                cancelText="Отмена"
            />
        </>
    );
}


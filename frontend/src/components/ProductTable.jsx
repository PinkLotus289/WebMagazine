import { Table, Button, Space, Modal, message } from 'antd';
import axios from 'axios';
import { useEffect, useState } from 'react';
import ProductForm from './ProductForm';

export default function ProductTable() {
    const [products, setProducts] = useState([]);
    const [editingProduct, setEditingProduct] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [toDelete, setToDelete] = useState(null);

    const fetchProducts = () => {
        axios.get('http://localhost:8080/products')
            .then(res => setProducts(res.data))
            .catch(console.error);
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    const handleEdit = (product) => {
        setEditingProduct(product);
        setIsModalOpen(true);
    };

    const columns = [
        {
            title: 'Название',
            dataIndex: 'name',
            width: '33%',
        },
        {
            title: 'Цена у.е.',
            dataIndex: 'price',
            width: '33%',
        },
        {
            title: 'Действия',
            width: '34%',
            render: (_, record) => (
                <Space>
                    <Button style={{ color: '#40e0d0' }} onClick={() => handleEdit(record)}>Изменить</Button>
                    <Button danger onClick={() => setToDelete(record)}>Удалить</Button>
                </Space>
            )
        }
    ];

    <Table
        dataSource={products}
        columns={columns}
        rowKey="id"
        pagination={{ pageSize: 10 }}
        style={{ width: '100%' }}
    />


    return (
        <>
            <Button
                style={{ marginBottom: 16, backgroundColor: '#40e0d0', color: 'black' }}
                onClick={() => {
                    setEditingProduct(null);
                    setIsModalOpen(true);
                }}
            >
                Добавить товар
            </Button>

            <Table
                dataSource={products}
                columns={columns}
                rowKey="id"
                pagination={{ pageSize: 10 }}
            />

            <ProductForm
                visible={isModalOpen}
                onCancel={() => setIsModalOpen(false)}
                onSuccess={() => {
                    setIsModalOpen(false);
                    fetchProducts();
                }}
                product={editingProduct}
            />

            <Modal
                open={!!toDelete}
                title={`Удалить "${toDelete?.name}"?`}
                onCancel={() => setToDelete(null)}
                onOk={() => {
                    axios.delete(`http://localhost:8080/products/${toDelete.id}`)
                        .then(() => {
                            message.success('Удалено');
                            fetchProducts();
                        })
                        .catch(err => {
                            if (err.response?.status === 409) {
                                Modal.info({
                                    title: 'Нельзя удалить товар',
                                    content: err.response.data?.error || 'Товар используется в заказах.',
                                });
                            } else {
                                console.error('❌ Ошибка при удалении:', err);
                                message.error('Ошибка при удалении');
                            }
                        })
                        .finally(() => setToDelete(null));
                }}
                okButtonProps={{ style: { backgroundColor: '#40e0d0', borderColor: '#40e0d0' } }}
                okText="Удалить"
                cancelText="Отмена"
            />
        </>
    );
}



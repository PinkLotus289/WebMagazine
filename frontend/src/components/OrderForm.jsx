import { Modal, Form, Input, message } from 'antd';
import axios from 'axios';
import { useEffect } from 'react';
import ProductSelector from './ProductSelector';

export default function OrderForm({ visible, onCancel, onSuccess, order }) {
    const [form] = Form.useForm();

    useEffect(() => {
        if (order) {
            const productIds = order.products.map(p => p.id);
            form.setFieldsValue({ ...order, products: productIds });
        } else {
            form.resetFields();
        }
    }, [order, form]);

    const handleSubmit = () => {
        form.validateFields().then(values => {
            const payload = {
                customerName: values.customerName,
                products: values.products.map(id => ({ id }))
            };

            const request = order
                ? axios.put(`http://localhost:8080/orders/${order.id}`, payload)
                : axios.post('http://localhost:8080/orders', payload);

            request
                .then(() => {
                    message.success(order ? 'Обновлено' : 'Создано');
                    onSuccess();
                })
                .catch((err) => {
                    console.error('Ошибка:', err.response?.data || err);
                    message.error('Ошибка при сохранении');
                });
        });
    };

    return (
        <Modal
            title={order ? 'Редактировать заказ' : 'Новый заказ'}
            open={visible}
            onCancel={onCancel}
            onOk={handleSubmit}
            okButtonProps={{ style: { backgroundColor: '#40e0d0', borderColor: '#40e0d0' } }}
            okText="Сохранить"
            cancelText="Отмена"
        >
            <Form form={form} layout="vertical">
                <Form.Item name="customerName" label="Имя клиента" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>
                <Form.Item name="products" label="Продукты" rules={[{ required: true }]}>
                    <ProductSelector />
                </Form.Item>
            </Form>
        </Modal>
    );
}

import {axios} from '../utils/request'
import {BOOk_MODULE} from './_prefix'

type AddInfo = {
    title: string,
    price: number,
    rate: number,
    description?: string,
    cover?: string,
    detail?: string,
    specifications?: string
}


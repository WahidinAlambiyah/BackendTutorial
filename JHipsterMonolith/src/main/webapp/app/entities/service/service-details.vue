<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="service">
        <h2 class="jh-entity-heading" data-cy="serviceDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.service.detail.title')"></span> {{ service.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.name')"></span>
          </dt>
          <dd>
            <span>{{ service.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.description')"></span>
          </dt>
          <dd>
            <span>{{ service.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.price')"></span>
          </dt>
          <dd>
            <span>{{ service.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.durationInHours')"></span>
          </dt>
          <dd>
            <span>{{ service.durationInHours }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.serviceType')"></span>
          </dt>
          <dd>
            <span v-text="t$('jHipsterMonolithApp.ServiceType.' + service.serviceType)"></span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.service.testimonial')"></span>
          </dt>
          <dd>
            <div v-if="service.testimonial">
              <router-link :to="{ name: 'TestimonialView', params: { testimonialId: service.testimonial.id } }">{{
                service.testimonial.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="service.id" :to="{ name: 'ServiceEdit', params: { serviceId: service.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./service-details.component.ts"></script>
